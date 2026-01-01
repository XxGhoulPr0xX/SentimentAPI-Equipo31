package com.equipo31.app.Service;

import ai.onnxruntime.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Servicio que se encarga de cargar el modelo ONNX y ejecutar las predicciones.
 * Lo hice como un @Service para que Spring lo maneje automáticamente.
 */
@Service
public class SentimentService {

    private static final Logger log = LoggerFactory.getLogger(SentimentService.class);

    @Value("${onnx.model.path:models/modelo_sentimientoENG.onnx}")
    private String modelPath;

    private OrtEnvironment env;
    private OrtSession session;
    private String inputName;

    // Las etiquetas que devuelve el modelo (es binario: 0=Negativo, 1=Positivo)
    private static final String[] ETIQUETAS = { "Negativo", "Positivo" };

    @PostConstruct
    public void inicializar() {
        try {
            log.info("Inicializando servicio de análisis de sentimiento...");

            // Crear entorno ONNX
            env = OrtEnvironment.getEnvironment();

            // Buscar el modelo en diferentes ubicaciones
            Path rutaModelo = encontrarModelo();

            if (rutaModelo == null) {
                throw new RuntimeException("No se encontró el modelo ONNX en ninguna ubicación");
            }

            log.info("Cargando modelo desde: {}", rutaModelo.toAbsolutePath());

            // Crear sesión con el modelo
            OrtSession.SessionOptions opciones = new OrtSession.SessionOptions();
            session = env.createSession(rutaModelo.toString(), opciones);

            // Obtener nombre de la entrada del modelo
            Map<String, NodeInfo> inputs = session.getInputInfo();
            inputName = inputs.keySet().iterator().next();

            log.info("Modelo cargado exitosamente. Entrada: {}", inputName);
            log.info("Salidas del modelo: {}", session.getOutputInfo().keySet());

        } catch (Exception e) {
            log.error("Error al cargar el modelo ONNX: {}", e.getMessage());
            throw new RuntimeException("Error al inicializar el modelo de sentimiento", e);
        }
    }

    /**
     * Busca el archivo del modelo en varias ubicaciones posibles.
     */
    private Path encontrarModelo() {
        // Lista de rutas donde buscar el modelo
        String[] ubicaciones = {
                modelPath,
                "app/" + modelPath,
                "../" + modelPath,
                "src/main/resources/static/models/modelo_sentimientoENG.onnx",
                "target/classes/static/models/modelo_sentimientoENG.onnx"
        };

        for (String ubicacion : ubicaciones) {
            Path ruta = Paths.get(ubicacion);
            if (Files.exists(ruta)) {
                return ruta;
            }
        }

        return null;
    }

    @PreDestroy
    public void cerrar() {
        try {
            if (session != null) {
                session.close();
            }
            log.info("Servicio de sentimiento cerrado correctamente");
        } catch (Exception e) {
            log.error("Error al cerrar el servicio: {}", e.getMessage());
        }
    }

    /**
     * Método principal que recibe el texto y devuelve la predicción.
     * Tuve que usar un array 2D porque el modelo lo requiere así.
     */
    public Map<String, Object> analizarSentimiento(String texto) {
        try {
            if (session == null) {
                throw new RuntimeException("El modelo no está cargado");
            }

            log.debug("Analizando texto: {}", texto);

            // Importante: el modelo espera un array 2D, no 1D. Me costó darme cuenta de
            // esto.
            String[][] inputData = new String[][] { { texto } };
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData);

            // Ejecutar inferencia
            Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, inputTensor);
            OrtSession.Result resultado = session.run(inputs);

            // Procesar salida
            Map<String, Object> respuesta = procesarResultado(resultado);

            // Cerrar recursos
            inputTensor.close();
            resultado.close();

            log.info("Resultado del análisis: {}", respuesta);
            return respuesta;

        } catch (Exception e) {
            log.error("Error al analizar sentimiento: {}", e.getMessage());
            // Si algo falla, devuelvo un resultado neutro para no romper la app
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("etiqueta", "Neutro");
            errorResult.put("probabilidad", 0.5);
            errorResult.put("error", e.getMessage());
            return errorResult;
        }
    }

    /**
     * Acá interpreto lo que devuelve el modelo (etiqueta y probabilidades).
     */
    private Map<String, Object> procesarResultado(OrtSession.Result resultado) throws OrtException {
        Map<String, Object> respuesta = new HashMap<>();

        // Los modelos sklearn-onnx típicamente tienen dos salidas:
        // - "label" o "output_label": la etiqueta predicha
        // - "probabilities" o "output_probability": las probabilidades

        for (Map.Entry<String, OnnxValue> entry : resultado) {
            String nombre = entry.getKey();
            OnnxValue valor = entry.getValue();

            log.debug("Procesando salida '{}': tipo={}", nombre, valor.getType());

            if (nombre.toLowerCase().contains("label")) {
                // Etiqueta predicha
                if (valor instanceof OnnxTensor) {
                    OnnxTensor tensor = (OnnxTensor) valor;
                    Object data = tensor.getValue();

                    if (data instanceof long[]) {
                        long[] labels = (long[]) data;
                        int idx = (int) labels[0];
                        respuesta.put("etiqueta", idx < ETIQUETAS.length ? ETIQUETAS[idx] : "Desconocido");
                    } else if (data instanceof String[]) {
                        String[] labels = (String[]) data;
                        respuesta.put("etiqueta", labels[0]);
                    } else {
                        respuesta.put("etiqueta", data.toString());
                    }
                }
            } else if (nombre.toLowerCase().contains("probab")) {
                // Probabilidades
                if (valor instanceof OnnxSequence) {
                    OnnxSequence seq = (OnnxSequence) valor;
                    List<?> lista = seq.getValue();
                    if (!lista.isEmpty() && lista.get(0) instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<Long, Float> probs = (Map<Long, Float>) lista.get(0);
                        // Encontrar la probabilidad máxima
                        float maxProb = 0f;
                        for (Float prob : probs.values()) {
                            if (prob > maxProb) {
                                maxProb = prob;
                            }
                        }
                        respuesta.put("probabilidad", (double) maxProb);
                    }
                } else if (valor instanceof OnnxTensor) {
                    OnnxTensor tensor = (OnnxTensor) valor;
                    float[][] probs = (float[][]) tensor.getValue();
                    if (probs.length > 0 && probs[0].length > 0) {
                        float maxProb = 0f;
                        for (float prob : probs[0]) {
                            if (prob > maxProb) {
                                maxProb = prob;
                            }
                        }
                        respuesta.put("probabilidad", (double) maxProb);
                    }
                }
            }
        }

        // Valores por defecto si no se encontraron
        if (!respuesta.containsKey("etiqueta")) {
            respuesta.put("etiqueta", "Neutro");
        }
        if (!respuesta.containsKey("probabilidad")) {
            respuesta.put("probabilidad", 0.5);
        }

        return respuesta;
    }
}

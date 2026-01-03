package com.equipo31.app.service;

import ai.onnxruntime.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Servicio que se encarga de cargar el modelo ONNX y ejecutar las predicciones.
 * Lo hice como un @Service para que Spring lo maneje automáticamente.
 */
@Service
public class SentimentService {

    private static final Logger log = LoggerFactory.getLogger(SentimentService.class);

    // @Value("${onnx.model.path:models/modelo_sentimientoESP.onnx}")
    @Value("${onnx.model.path.es:models/modelo_sentimientoESP.onnx}")
    private String modelPathEs;
    @Value("${onnx.model.path.en:models/modelo_sentimientoENG.onnx}")
    private String modelPathEn;

    private OrtEnvironment env;
    private OrtSession session;
    // private String inputName;

    // Mapas hash que son para guardar las sesiones actuales de los modelos de ia
    private Map<String, OrtSession> sesiones = new HashMap<>();
    private Map<String, String> inputNames = new HashMap<>();

    private String idiomaActual = "es";

    // Las etiquetas que devuelve el modelo (es binario: 0=Negativo, 1=Positivo)
    private static final String[] ETIQUETAS = { "Negativo", "Positivo" };

    // Inicia la busqueda del modelo de ia y se carga en memoria
    @PostConstruct
    public void inicializar() {
        try {
            env = OrtEnvironment.getEnvironment();
            // Cargar modelo ESPAÑOL
            cargarModelo("es", modelPathEs);
            // Cargar modelo INGLES
            cargarModelo("en", modelPathEn);
            log.info("Modelos cargados. Idioma inicial: {}", idiomaActual);
        } catch (Exception e) {
            log.error("Error inicializando modelos: {}", e.getMessage());
        }
    }

    /**
     * Al tener una ruta dentro de un disco, no servia si se llevaba a un servidor,
     * entonces
     * se aplico la tecnica de classpath, que esta genera un archivo temporal dentro
     * de recursos que accede el mismo framework
     */
    private void cargarModelo(String claveIdioma, String rutaArchivo) throws Exception {
        try {
            ClassPathResource resource = new ClassPathResource("static/" + rutaArchivo);
            if (resource.exists()) {
                // ONNX Runtime necesita un archivo físico en el disco o un array de bytes.
                // Lo más seguro es crear un archivo temporal para que la librería pueda leerlo.
                Path tempFile = Files.createTempFile("modelo_" + claveIdioma, ".onnx");
                try (InputStream is = resource.getInputStream()) {
                    Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }

                // 2. Crear la sesión usando la ruta del archivo temporal
                OrtSession session = env.createSession(tempFile.toString(), new OrtSession.SessionOptions());
                sesiones.put(claveIdioma, session);

                String inputName = session.getInputInfo().keySet().iterator().next();
                inputNames.put(claveIdioma, inputName);

                log.info("Modelo cargado [{}] exitosamente desde el Classpath", claveIdioma);

                // Opcional: borrar el archivo temporal al salir
                tempFile.toFile().deleteOnExit();
            } else {
                log.error("No se encontró el recurso en el classpath para: static/{}", rutaArchivo);
            }
        } catch (Exception e) {
            log.error("Error cargando el modelo [{}]: {}", claveIdioma, e.getMessage());
            throw e;
        }
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

    // --- NUEVO MÉTODO PARA CAMBIAR EL IDIOMA DESDE EL CONTROLADOR ---
    public void setIdioma(String nuevoIdioma) {
        if (sesiones.containsKey(nuevoIdioma)) {
            this.idiomaActual = nuevoIdioma;
            log.info("Idioma cambiado a: {}", nuevoIdioma);
        } else {
            throw new IllegalArgumentException("Idioma no soportado o modelo no cargado: " + nuevoIdioma);
        }
    }

    public String getIdiomaActual() {
        return this.idiomaActual;
    }

    /**
     * Método principal que recibe el texto y devuelve la predicción.
     * Tuve que usar un array 2D porque el modelo lo requiere así.
     */
    public Map<String, Object> analizarSentimiento(String texto) {
        OrtSession session = sesiones.get(idiomaActual);
        String inputName = inputNames.get(idiomaActual);
        if (session == null) {
            throw new RuntimeException("El modelo no está cargado");
        }
        try {
            String[][] inputData = new String[][] { { texto } };
            OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData);
            Map<String, OnnxTensor> inputs = Collections.singletonMap(inputName, inputTensor);
            OrtSession.Result resultado = session.run(inputs);
            Map<String, Object> respuesta = procesarResultado(resultado); // Tu método existente
            inputTensor.close();
            resultado.close();
            return respuesta;
        } catch (Exception e) {
            log.error("Error al analizar sentimiento: {}", e.getMessage());
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
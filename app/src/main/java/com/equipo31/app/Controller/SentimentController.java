package com.equipo31.app.Controller;

import com.equipo31.app.dto.SentimentRequest;
import com.equipo31.app.dto.SentimentResponse;
import com.equipo31.app.entity.SentimentRecord;

import com.equipo31.app.repository.SentimentRecordRepository;
import com.equipo31.app.Service.AnalizarArchivoService;
import com.equipo31.app.Service.SentimentService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/sentiment-api")
public class SentimentController {

    private static final Logger log = LoggerFactory.getLogger(SentimentController.class);
    private final SentimentRecordRepository repo;
    private final SentimentService sentimentService;
    private final AnalizarArchivoService archivoService; // Nuevo servicio inyectado

    public SentimentController(SentimentRecordRepository repo, SentimentService sentimentService,
            AnalizarArchivoService archivoService) {
        this.repo = repo;
        this.sentimentService = sentimentService;
        this.archivoService = archivoService;
    }

    @PostMapping("/analizar-comentario")
    public ResponseEntity<?> analizar(@Valid @RequestBody SentimentRequest req) {
        try {
            // Validar que el comentario no esté vacío o solo contenga espacios
            if (req.getText() == null || req.getText().trim().isEmpty()) {
                log.warn("Comentario vacío o solo espacios recibido");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El comentario no puede estar vacío o contener solo espacios."));
            }

            log.info("Comentario recibido: {}", req.getText());

            // Usar el servicio de IA para analizar el sentimiento
            Map<String, Object> resultado = sentimentService.analizarSentimiento(req.getText().trim());

            String label = (String) resultado.get("etiqueta");
            double prob = (Double) resultado.get("probabilidad");

            log.info("Resultado del análisis - Etiqueta: {}, Probabilidad: {}", label, prob);

            // Guardar el registro en la base de datos
            SentimentRecord sentimentRecord = repo.save(new SentimentRecord(req.getText(), label, prob));

            return ResponseEntity.ok(new SentimentResponse(
                    sentimentRecord.getId(),
                    sentimentRecord.getText(),
                    sentimentRecord.getPrevision(),
                    sentimentRecord.getProbabilidad(),
                    sentimentRecord.getCreatedAt()));
        } catch (Exception e) {
            log.error("Error al analizar comentario: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al analizar el comentario: " + e.getMessage()));
        }
    }

    @PostMapping("/analizar-archivo")
    public ResponseEntity<?> analizarCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam("columnName") String columnName) {
        try {
            // Validar que el nombre de columna no esté vacío o solo contenga espacios
            if (columnName == null || columnName.trim().isEmpty()) {
                log.warn("Nombre de columna vacío o solo espacios recibido");
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "El nombre de la columna no puede estar vacío o contener solo espacios."));
            }

            // 1. Obtener solo la lista de comentarios del servicio de archivos
            List<String> comentarios = archivoService.extraerComentariosDeCSV(file, columnName.trim());

            // Filtrar comentarios vacíos o que solo contengan espacios
            comentarios = comentarios.stream()
                    .filter(c -> c != null && !c.trim().isEmpty())
                    .map(String::trim)
                    .toList();

            // Verificar si hay comentarios válidos para analizar
            if (comentarios.isEmpty()) {
                log.warn("No se encontraron comentarios válidos en el archivo CSV");
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "No se encontraron comentarios válidos en el archivo. Verifica que la columna tenga datos."));
            }

            List<SentimentResponse> respuestas = new ArrayList<>();

            // 2. Procesar cada comentario individualmente en el controlador
            for (String texto : comentarios) {
                // Lógica del servicio de IA
                Map<String, Object> resultado = sentimentService.analizarSentimiento(texto);
                String label = (String) resultado.get("etiqueta");
                double prob = (Double) resultado.get("probabilidad");

                // Lógica del repositorio
                SentimentRecord record = repo.save(new SentimentRecord(texto, label, prob));

                // Mapear a respuesta
                respuestas.add(new SentimentResponse(
                        record.getId(),
                        record.getText(),
                        record.getPrevision(),
                        record.getProbabilidad(),
                        record.getCreatedAt()));
            }

            return ResponseEntity.ok(respuestas);
        } catch (RuntimeException e) {
            // Errores específicos del servicio (columna no encontrada, archivo vacío, etc.)
            log.error("Error de validación en archivo CSV: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error al analizar archivo CSV: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al analizar el archivo: " + e.getMessage()));
        }
    }
}

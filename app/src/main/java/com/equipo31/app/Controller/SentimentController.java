package com.equipo31.app.Controller;

import com.equipo31.app.dto.SentimentRequest;
import com.equipo31.app.dto.SentimentResponse;
import com.equipo31.app.entity.SentimentRecord;
import com.equipo31.app.repository.SentimentRecordRepository;
import com.equipo31.app.Service.SentimentService;
import com.opencsv.CSVReader;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sentiment-api")
public class SentimentController {

    private static final Logger log = LoggerFactory.getLogger(SentimentController.class);
    private final SentimentRecordRepository repo;
    private final SentimentService sentimentService;

    public SentimentController(SentimentRecordRepository repo, SentimentService sentimentService) {
        this.repo = repo;
        this.sentimentService = sentimentService;
    }

    @PostMapping("/analizar-comentario")
    public ResponseEntity<SentimentResponse> analizar(@Valid @RequestBody SentimentRequest req) {
        log.info("Comentario recibido: {}", req.getText());

        // Usar el servicio de IA para analizar el sentimiento
        Map<String, Object> resultado = sentimentService.analizarSentimiento(req.getText());

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
    }

    @PostMapping("/analizar-archivo")
    public ResponseEntity<List<SentimentResponse>> analizarCSV(
            @RequestParam("file") MultipartFile file,
            @RequestParam("columnName") String columnName) throws Exception {

        List<SentimentResponse> respuestas = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReader(reader);
            String[] cabecera = csvReader.readNext();

            int columnIndex = -1;
            for (int i = 0; i < cabecera.length; i++) {
                if (cabecera[i].equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                csvReader.close();
                throw new RuntimeException("La columna " + columnName + " no se encontró en el archivo.");
            }

            String[] fila;
            while ((fila = csvReader.readNext()) != null) {
                if (columnIndex < fila.length) {
                    String comentario = fila[columnIndex];

                    Map<String, Object> resultado = sentimentService.analizarSentimiento(comentario);
                    String label = (String) resultado.get("etiqueta");
                    double prob = (Double) resultado.get("probabilidad");

                    SentimentRecord record = repo.save(new SentimentRecord(comentario, label, prob));
                    respuestas.add(new SentimentResponse(
                            record.getId(),
                            record.getText(),
                            record.getPrevision(),
                            record.getProbabilidad(),
                            record.getCreatedAt()));
                }
            }
            csvReader.close();
        }
        return ResponseEntity.ok(respuestas);
    }
}

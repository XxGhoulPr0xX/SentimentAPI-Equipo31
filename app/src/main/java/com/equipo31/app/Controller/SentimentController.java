package com.equipo31.app.controller;

import com.equipo31.app.dto.SentimentRequest;
import com.equipo31.app.dto.SentimentResponse;
import com.equipo31.app.entity.SentimentRecord;
import com.equipo31.app.repository.SentimentRecordRepository;
import com.equipo31.app.service.SentimentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class SentimentController {

    private static final Logger log = LoggerFactory.getLogger(SentimentController.class);
    private final SentimentRecordRepository repo;
    private final SentimentService sentimentService;

    public SentimentController(SentimentRecordRepository repo, SentimentService sentimentService) {
        this.repo = repo;
        this.sentimentService = sentimentService;
    }

    @PostMapping("/sentiment")
    public ResponseEntity<SentimentResponse> analizar(@Valid @RequestBody SentimentRequest req) {
        log.info("Comentario recibido: {}", req.getText());

        // Usar el servicio de IA para analizar el sentimiento
        Map<String, Object> resultado = sentimentService.analizarSentimiento(req.getText());

        String label = (String) resultado.get("etiqueta");
        double prob = (Double) resultado.get("probabilidad");

        log.info("Resultado del análisis - Etiqueta: {}, Probabilidad: {}", label, prob);

        // Guardar el registro en la base de datos
        repo.save(new SentimentRecord(req.getText(), label, prob));

        return ResponseEntity.ok(new SentimentResponse(label, prob));
    }
}

package com.equipo31.app.Controller;

import com.equipo31.app.dto.SentimentRequest;
import com.equipo31.app.dto.SentimentResponse;
import com.equipo31.app.entity.SentimentRecord;
import com.equipo31.app.repository.SentimentRecordRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class SentimentController {

    private static final Logger log = LoggerFactory.getLogger(SentimentController.class);
    private final SentimentRecordRepository repo;

    public SentimentController(SentimentRecordRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/sentiment")
    public ResponseEntity<SentimentResponse> analizar(@Valid @RequestBody SentimentRequest req) {
        log.info("Comentario recibido: {}", req.getText());

        String label = "Neutro";
        double prob = 0.50;

        repo.save(new SentimentRecord(req.getText(), label, prob));

        return ResponseEntity.ok(new SentimentResponse(label, prob));
    }
}

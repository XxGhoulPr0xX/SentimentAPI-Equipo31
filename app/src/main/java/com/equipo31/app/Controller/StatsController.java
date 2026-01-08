package com.equipo31.app.Controller;

import com.equipo31.app.entity.SentimentRecord;
import com.equipo31.app.repository.SentimentRecordRepository;
import com.equipo31.app.Service.EstadisticasRegistros;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final SentimentRecordRepository repo;
    private final EstadisticasRegistros estadisticasService;

    public StatsController(SentimentRecordRepository repo, EstadisticasRegistros estadisticasService) {
        this.repo = repo;
        this.estadisticasService = estadisticasService;
    }

    // Endpoint que se encarga de enviar el formato json de la base de datos h2,
    // contiene "ID", "FECHA", "SENTIMIENTO", "PROB", "TEXTO"
    @GetMapping("/list")
    public ResponseEntity<List<SentimentRecord>> imprimirRegistrosEnConsola() {
        List<SentimentRecord> registros = repo.findAll();
        estadisticasService.imprimirRegistrosEnConsola(registros);
        return ResponseEntity.ok(registros);
    }
}
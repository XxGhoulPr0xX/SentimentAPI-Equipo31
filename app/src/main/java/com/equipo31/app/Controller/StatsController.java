package com.equipo31.app.Controller;

import com.equipo31.app.dto.StatsResponse;
import com.equipo31.app.repository.SentimentRecordRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class StatsController {

    private final SentimentRecordRepository repo;

    public StatsController(SentimentRecordRepository repo) {
        this.repo = repo;
    }

    @GetMapping("stats")
    public StatsResponse stats() {
        long total = repo.count();
        long positivos = repo.countByPrevision("Positivo");
        long negativos = repo.countByPrevision("Negativo");
        long neutros = repo.countByPrevision("Neutro");
        return new StatsResponse(total, positivos, negativos, neutros);
    }
}

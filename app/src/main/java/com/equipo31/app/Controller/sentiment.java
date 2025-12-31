package com.equipo31.app.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.equipo31.app.Dto.ResponseSentimentDto;
import com.equipo31.app.Dto.SentimentDto;

@RestController
public class sentiment {
    @PostMapping("/sentiment")
    public ResponseEntity<ResponseSentimentDto> analizar(@RequestBody SentimentDto body) {
        // Aquí se envia la peticion en json al endpoint que gestiona el modelo de IA
        return ResponseEntity.ok(new ResponseSentimentDto(1, body.comentario(), "positivo", 0.95));
    }
}

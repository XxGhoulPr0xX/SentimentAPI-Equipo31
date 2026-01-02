package com.equipo31.app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.equipo31.app.Service.SentimentService;


/**
 * Ruta que se encarga de cargar la logica de cambio de idioma del servicio de SentimientService.
 */
@RestController
@RequestMapping("/api/config")
public class ApiConfig {

    @Autowired
    private SentimentService sentimentService;

    @PostMapping("/idioma")
    public ResponseEntity<String> cambiarIdioma(@RequestParam String lang) {
        try {
            sentimentService.setIdioma(lang.toLowerCase());
            return ResponseEntity.ok("Idioma cambiado a: " + lang);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
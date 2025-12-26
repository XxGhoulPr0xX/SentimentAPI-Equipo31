package com.equipo31.app.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class sentimient {
    @PostMapping("/sentiment")
    public String procesarSentiment(@RequestParam String texto) {
        // Aquí se envia la peticion en json al endpoint que gestiona el modelo de IA
        return "index"; 
    }
}

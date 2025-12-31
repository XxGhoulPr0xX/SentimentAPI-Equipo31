package com.equipo31.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class aboutit {

    // Renderiza la plantilla index del templates
    @GetMapping("/acerca")
    public String mostrarPagina() {
        return "aboutit";
    }

}

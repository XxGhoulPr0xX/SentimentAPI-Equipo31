package com.equipo31.app.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class index {

    // Redirecciona a la index
    @GetMapping("/")
    public String redireccionar() {
        return "redirect:/index";
    }

    // Renderiza la plantilla index del templates
    @GetMapping("/index")
    public String mostrarPagina() {
        return "index";
    }

}

package com.equipo31.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SentimentRequest {

    @NotBlank(message = "El campo 'text' es obligatorio.")
    @Size(min = 3, message = "El texto debe tener al menos 3 caracteres.")
    private String text;

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}

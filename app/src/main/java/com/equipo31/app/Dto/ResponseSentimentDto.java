package com.equipo31.app.Dto;

public record ResponseSentimentDto(
        Integer id,
        String comentario,
        String sentimiento,
        double probabilidad) {
    public ResponseSentimentDto(Integer id, String comentario, String sentimiento, double probabilidad) {
        this.id = id;
        this.sentimiento = sentimiento;
        this.probabilidad = probabilidad;
        this.comentario = comentario;
    }
}

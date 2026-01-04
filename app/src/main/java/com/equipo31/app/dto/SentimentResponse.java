package com.equipo31.app.dto;

import java.time.LocalDateTime;

public class SentimentResponse {
    private Long id;
    private String text;
    private String prevision;
    private double probabilidad;
    private LocalDateTime createdAt;

    public SentimentResponse(Long id, String text, String prevision, double probabilidad, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.prevision = prevision;
        this.probabilidad = probabilidad;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getPrevision() {
        return prevision;
    }

    public double getProbabilidad() {
        return probabilidad;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

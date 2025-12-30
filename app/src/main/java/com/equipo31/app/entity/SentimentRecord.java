package com.equipo31.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SentimentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String text;

    private String prevision;
    private double probabilidad;

    private LocalDateTime createdAt = LocalDateTime.now();

    public SentimentRecord() {}

    public SentimentRecord(String text, String prevision, double probabilidad) {
        this.text = text;
        this.prevision = prevision;
        this.probabilidad = probabilidad;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public String getPrevision() { return prevision; }
    public double getProbabilidad() { return probabilidad; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

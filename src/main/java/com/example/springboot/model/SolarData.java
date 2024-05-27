package com.example.springboot.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solar_generation")
public class SolarData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pes_id")
    private Integer pesId;

    @Column(name = "datetime_gmt")
    private LocalDateTime datetimeGMT;

    @Column(name = "generation_mw")
    private Double generationMW;

    // Constructori, getteri și setteri
    public SolarData() {
    }

    public SolarData(Integer pesId, LocalDateTime datetimeGMT, Double generationMW) {
        this.pesId = pesId;
        this.datetimeGMT = datetimeGMT;
        this.generationMW = generationMW;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPesId() {
        return pesId;
    }

    public void setPesId(Integer pesId) {
        this.pesId = pesId;
    }

    public LocalDateTime getDatetimeGMT() {
        return datetimeGMT;
    }

    public void setDatetimeGMT(LocalDateTime datetimeGMT) {
        this.datetimeGMT = datetimeGMT;
    }

    public Double getGenerationMW() {
        return generationMW;
    }

    public void setGenerationMW(Double generationMW) {
        this.generationMW = generationMW;
    }
}
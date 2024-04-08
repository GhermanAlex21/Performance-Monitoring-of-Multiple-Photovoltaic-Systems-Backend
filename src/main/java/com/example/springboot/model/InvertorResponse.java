package com.example.springboot.model;

// Definirea clasei InvertorResponse
public class InvertorResponse {
    private Long id;
    private Long marcaId;
    private Long serieId;
    private double latitude;
    private double longitude;
    private double azimut;

    // Constructor, getteri și setteri
    public InvertorResponse() {
    }

    public InvertorResponse(Long id, Long marcaId, Long serieId, double latitude, double longitude, double azimut) {
        this.id = id;
        this.marcaId = marcaId;
        this.serieId = serieId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.azimut = azimut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Long marcaId) {
        this.marcaId = marcaId;
    }

    public Long getSerieId() {
        return serieId;
    }

    public void setSerieId(Long serieId) {
        this.serieId = serieId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAzimut() {
        return azimut;
    }

    public void setAzimut(double azimut) {
        this.azimut = azimut;
    }
}
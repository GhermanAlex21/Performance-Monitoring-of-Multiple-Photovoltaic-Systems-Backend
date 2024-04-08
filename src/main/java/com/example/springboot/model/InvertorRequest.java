package com.example.springboot.model;

public class InvertorRequest {
    private Long serieId;
    private Long marcaId;
    private double latitude;
    private double longitude;
    private double azimut;

    // Constructori, getteri și setteri

    public InvertorRequest() {
    }

    public InvertorRequest(Long serieId, Long marcaId, double latitude, double longitude, double azimut) {
        this.serieId = serieId;
        this.marcaId = marcaId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.azimut = azimut;
    }

    public Long getSerieId() {
        return serieId;
    }

    public void setSerieId(Long serieId) {
        this.serieId = serieId;
    }

    public Long getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(Long marcaId) {
        this.marcaId = marcaId;
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

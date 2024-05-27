package com.example.springboot.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "invertor")
public class Invertor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_marca")
    private Marca marca;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_serie")
    private Serie serie;

    @Column(name = "lat")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "azimut")
    private double azimut;

    @Column(name = "pes_id", unique = true)
    private Integer pesId;  // Acest câmp va fi folosit pentru a corela invertorul cu datele solare

    public Invertor() {}

    public Invertor(Marca marca, Serie serie, double latitude, double longitude, double azimut, Integer pesId) {
        this.marca = marca;
        this.serie = serie;
        this.latitude = latitude;
        this.longitude = longitude;
        this.azimut = azimut;
        this.pesId = pesId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
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

    public Integer getPesId() {
        return pesId;
    }

    public void setPesId(Integer pesId) {
        this.pesId = pesId;
    }
}

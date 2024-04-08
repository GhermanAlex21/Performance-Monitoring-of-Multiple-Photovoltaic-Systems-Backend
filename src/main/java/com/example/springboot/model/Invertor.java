package com.example.springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "invertor")
public class Invertor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // Modificare aici pentru a încărca relația automat
    @JoinColumn(name = "id_marca")
    private Marca marca;

    @ManyToOne(fetch = FetchType.EAGER, optional = false) // Modificare aici pentru a încărca relația automat
    @JoinColumn(name = "id_serie")
    private Serie serie;

    @Column(name = "lat")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "azimut")
    private double azimut;

    // Constructori
    public Invertor() {
    }

    public Invertor(Marca marca, Serie serie, double latitude, double longitude, double azimut) {
        this.marca = marca;
        this.serie = serie;
        this.latitude = latitude;
        this.longitude = longitude;
        this.azimut = azimut;
    }

    // Getteri și setteri
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
}

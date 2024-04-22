package com.example.springboot.model;


import jakarta.persistence.*;

@Entity
@Table(name = "serii_invertoare")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nume")
    private String nume;

    // Constructori
    public Serie() {
    }

    public Serie(String nume) {
        this.nume = nume;
    }

    // Getteri și setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

}
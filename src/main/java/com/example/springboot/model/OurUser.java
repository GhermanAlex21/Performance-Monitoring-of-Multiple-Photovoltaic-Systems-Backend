package com.example.springboot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@Entity
@Table(name = "ourusers")
public class OurUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nume;

    @Column(nullable = false)
    private String prenume;

    @Column(nullable = false, length = 15)
    private String telefon;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Column(nullable = false)
    private String roles;

    public OurUser() {
    }

    public OurUser(Integer id, String nume, String prenume, String telefon, String username, String password, String roles) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.telefon = telefon;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
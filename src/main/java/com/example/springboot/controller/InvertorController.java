package com.example.springboot.controller;

import com.example.springboot.model.*;
import com.example.springboot.service.InvertorService;
import com.example.springboot.service.MarcaService;
import com.example.springboot.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")

public class InvertorController {

    @Autowired
    private InvertorService invertorService;


    @Autowired
    private SerieService serieService; // Injectăm SerieService

    @Autowired
    private MarcaService marcaService; // Injectăm MarcaService

    @PostMapping("/invertors")
    public Invertor addInvertor(@RequestBody InvertorRequest invertorRequest) {
        // Obțineți id-urile seriei și mărcii din obiectul de cerere
        Long serieId = invertorRequest.getSerieId();
        Long marcaId = invertorRequest.getMarcaId();

        // Obțineți obiectele de serie și marcă folosind id-urile

        Serie serie = serieService.findById(serieId);
        Marca marca = marcaService.findById(marcaId);

        // Creați un nou obiect Invertor cu seria și marca corecte
        Invertor invertor = new Invertor();
        invertor.setSerie(serie);
        invertor.setMarca(marca);
        invertor.setLatitude(invertorRequest.getLatitude());
        invertor.setLongitude(invertorRequest.getLongitude());
        invertor.setAzimut(invertorRequest.getAzimut());

        // Salvarea și returnarea invertorului creat
        return invertorService.saveInvertor(invertor);
    }

    @GetMapping("/get_invertori") // Endpoint pentru a obține toți invertorii
    public List<Invertor> getAllInvertors() {
        return invertorService.getAllInvertors();
    }
    @DeleteMapping("/delete_invertor/{id}") // Endpoint pentru a șterge un invertor în funcție de ID
    public void deleteInvertor(@PathVariable Long id) {
        invertorService.deleteInvertor(id);
    }

    @GetMapping("/invertors/{id}") // Endpoint pentru a obține un invertor după ID
    public ResponseEntity<Invertor> getInvertorById(@PathVariable Long id) {
        Invertor invertor = invertorService.findById(id);
        if (invertor != null) {
            return new ResponseEntity<>(invertor, HttpStatus.OK); // Returnăm un răspuns cu invertorul găsit și statusul HTTP 200 OK
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returnăm un răspuns cu statusul HTTP 404 Not Found dacă invertorul nu este găsit
        }
    }

    @PutMapping("/update_invertor/{id}")
    public ResponseEntity<Invertor> updateInvertorById(@PathVariable Long id, @RequestBody InvertorRequest updatedInvertorRequest) {
        // Obținem invertorul existent din baza de date
        Invertor existingInvertor = invertorService.findById(id);
        if (existingInvertor != null) {
            // Actualizăm doar latitudinea, longitudinea și azimutul cu valorile din cererea de actualizare
            existingInvertor.setLatitude(updatedInvertorRequest.getLatitude());
            existingInvertor.setLongitude(updatedInvertorRequest.getLongitude());
            existingInvertor.setAzimut(updatedInvertorRequest.getAzimut());

            // Salvăm invertorul actualizat în baza de date
            Invertor updatedInvertor = invertorService.saveInvertor(existingInvertor);
            return new ResponseEntity<>(updatedInvertor, HttpStatus.OK); // Returnăm invertorul actualizat și statusul HTTP 200 OK
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returnăm un răspuns cu statusul HTTP 404 Not Found dacă invertorul nu este găsit
        }
    }

}

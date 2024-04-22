package com.example.springboot.controller;

import com.example.springboot.model.Invertor;
import com.example.springboot.model.Marca;
import com.example.springboot.model.ResponseMessage;
import com.example.springboot.model.Serie;
import com.example.springboot.service.FirebaseService;
import com.example.springboot.service.InvertorService;
import com.example.springboot.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "*")
public class SerieController {

    @Autowired
    private SerieService serieService;
    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private InvertorService invertorService;

    @PostMapping("/series")
    public ResponseEntity<?> addSerie(@RequestBody Serie serie) {
        try {
            Serie savedSerie = serieService.saveSerie(serie);
            String firebaseResult = firebaseService.addSerie(savedSerie);
            return ResponseEntity.ok("Serie saved successfully in MySQL and Firebase with timestamp: " + firebaseResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving serie: " + e.getMessage());
        }
    }


    @GetMapping("/get_serii") // Endpoint pentru a obține toate serii
    public ResponseEntity<List<Serie>> getAllSeries() {
        try {
            List<Serie> series = firebaseService.getAllSeries();
            return ResponseEntity.ok(series);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete_serie/{id}")
    public ResponseEntity<?> deleteSerie(@PathVariable Long id) {
        try {
            Serie serie = serieService.findById(id);
            if (serie == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serie not found");
            }

            // Șterge seria din Firestore
            firebaseService.deleteSerie(String.valueOf(id));

            // Actualizează starea seriilor și a invertorilor legați de aceasta în baza de date locală
            serieService.deleteSerie(id);

            return ResponseEntity.ok().body("Serie and related invertors deleted from both MySQL and Firebase.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting serie: " + e.getMessage());
        }
    }

    @GetMapping("/serie/{id}")
    public ResponseEntity<Serie> getSerieById(@PathVariable Long id) {
        try {
            Serie serie = firebaseService.getSerieById(id.toString());
            if (serie != null) {
                return ResponseEntity.ok(serie);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PutMapping("/update_serie/{id}")
    public ResponseEntity<?> updateSerie(@PathVariable Long id, @RequestBody Serie serieDetails) {
        try {
            // Obține seria curentă folosind ID-ul
            Serie existingSerie = serieService.findById(id);
            if (existingSerie == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Serie not found");
            }

            // Actualizează informațiile seriei
            existingSerie.setNume(serieDetails.getNume());

            // Salvează seria actualizată în baza de date MySQL
            Serie updatedSerie = serieService.updateSerie(id, existingSerie);

            // Actualizează seria și în Firebase
            String firebaseUpdateResult = firebaseService.updateSerie(updatedSerie);

            // Actualizează toate învertoarele care folosesc această serie
            updateInvertorsForSerie(id, updatedSerie);

            return ResponseEntity.ok("Serie updated successfully in MySQL and Firebase with timestamp: " + firebaseUpdateResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating serie: " + e.getMessage());
        }
    }

    private void updateInvertorsForSerie(Long serieId, Serie updatedSerie) throws ExecutionException, InterruptedException {
        List<Invertor> invertors = invertorService.findBySerieId(serieId);
        for (Invertor invertor : invertors) {
            invertor.setSerie(updatedSerie);
            invertorService.saveInvertor(invertor);  // Salvează actualizarea în baza de date locală
            firebaseService.updateInvertor(invertor);  // Actualizează în Firebase
        }
    }
}

package com.example.springboot.controller;

import com.example.springboot.model.*;
import com.example.springboot.service.FirebaseService;
import com.example.springboot.service.InvertorService;
import com.example.springboot.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin(origins = "*")

public class MarcaController {

    @Autowired
    private MarcaService marcaService;
    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private InvertorService invertorService;
    @PostMapping("/adauga_marca")
    public ResponseEntity<?> addMarca(@RequestBody Marca marca) {
        try {
            // Salvează marca în MySQL
            Marca savedMarca = marcaService.saveMarca(marca);
            // Încearcă să salvezi marca și în Firebase
            String firebaseResult = firebaseService.addMarca(savedMarca);
            return ResponseEntity.ok(new ResponseMessage("Marca was saved successfully in MySQL and Firebase at: " + firebaseResult));
        } catch (Exception e) {
            // Tratează excepția specifică și returnează un răspuns corespunzător
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("An error occurred: " + e.getMessage()));
        }
    }
    @GetMapping("/get_marca") // Endpoint pentru a obține toate mărcile
    public ResponseEntity<List<Marca>> getAllMarcas() {
        try {
            List<Marca> marcas = firebaseService.getAllMarcas();
            return ResponseEntity.ok(marcas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/delete_marca/{id}")
    public ResponseEntity<?> deleteMarca(@PathVariable Long id) {
        try {
            Marca marca = marcaService.findById(id);

            // Încearcă să ștergi marca din Firebase indiferent dacă este găsită în MySQL
            firebaseService.deleteMarca(id.toString());

            // Dacă marca este găsită în MySQL, șterge-o și de acolo
            if (marca != null) {
                marcaService.deleteMarca(id);
                return ResponseEntity.ok().body("Marca deleted successfully from both MySQL and Firebase.");
            } else {
                // Marca nu este găsită în MySQL, dar a fost ștearsă din Firebase
                return ResponseEntity.ok().body("Marca deleted from Firebase, not found in MySQL.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting marca: " + e.getMessage());
        }
    }

    @GetMapping("/marca/{id}")
    public ResponseEntity<Marca> getMarcaById(@PathVariable Long id) {
        try {
            Marca marca = firebaseService.getMarcaById(id.toString());
            if (marca != null) {
                return ResponseEntity.ok(marca);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @PutMapping("/update_marca/{id}")
    public ResponseEntity<?> updateMarca(@PathVariable Long id, @RequestBody Marca marcaDetails) {
        try {
            // Obține marca curentă folosind ID-ul
            Marca existingMarca = marcaService.findById(id);
            if (existingMarca == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Marca not found");
            }

            // Actualizează informațiile mărcii
            existingMarca.setNume(marcaDetails.getNume());

            // Salvează marca actualizată în baza de date MySQL
            Marca updatedMarca = marcaService.updateMarca(id, existingMarca);

            // Actualizează marca și în Firebase
            String firebaseUpdateResult = firebaseService.updateMarca(updatedMarca);

            // Actualizează toate învertoarele care folosesc această marcă
            updateInvertorsForMarca(id, updatedMarca);

            return ResponseEntity.ok("Marca updated successfully in MySQL and Firebase with timestamp: " + firebaseUpdateResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating marca: " + e.getMessage());
        }
    }


    private void updateInvertorsForMarca(Long marcaId, Marca updatedMarca) throws ExecutionException, InterruptedException {
        List<Invertor> invertors = invertorService.findByMarcaId(marcaId);
        for (Invertor invertor : invertors) {
            invertor.setMarca(updatedMarca);
            invertorService.saveInvertor(invertor);  // Salvează actualizarea în baza de date locală
            firebaseService.updateInvertor(invertor);  // Actualizează în Firebase
        }
    }
}

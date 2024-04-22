package com.example.springboot.controller;

import com.example.springboot.model.*;
import com.example.springboot.service.FirebaseService;
import com.example.springboot.service.InvertorService;
import com.example.springboot.service.MarcaService;
import com.example.springboot.service.SerieService;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
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
    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/invertors")
    public ResponseEntity<?> addInvertor(@RequestBody InvertorRequest invertorRequest) {
        try {
            // Obțineți id-urile seriei și mărcii din obiectul de cerere
            Long serieId = invertorRequest.getSerieId();
            Long marcaId = invertorRequest.getMarcaId();

            // Obțineți obiectele de serie și marcă folosind id-urile
            Serie serie = serieService.findById(serieId);
            Marca marca = marcaService.findById(marcaId);

            if (serie == null || marca == null) {
                return ResponseEntity.badRequest().body("Serie or Marca does not exist");
            }

            // Creați un nou obiect Invertor cu seria și marca corecte
            Invertor invertor = new Invertor();
            invertor.setSerie(serie);
            invertor.setMarca(marca);
            invertor.setLatitude(invertorRequest.getLatitude());
            invertor.setLongitude(invertorRequest.getLongitude());
            invertor.setAzimut(invertorRequest.getAzimut());

            // Salvarea în MySQL
            Invertor savedInvertor = invertorService.saveInvertor(invertor);

            // Salvarea în Firebase și obținerea timpului de actualizare
            String firebaseUpdateTime = firebaseService.addInvertor(savedInvertor);

            return ResponseEntity.ok("Invertor saved successfully in MySQL and Firebase at: " + firebaseUpdateTime);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/get_invertori") // Endpoint pentru a obține toți invertorii
    public ResponseEntity<List<Invertor>> getAllInvertors() {
        List<Invertor> invertors = new ArrayList<>();
        try {
            Firestore db = FirestoreClient.getFirestore();
            ApiFuture<QuerySnapshot> future = db.collection("invertors").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                invertors.add(document.toObject(Invertor.class));
            }
            return new ResponseEntity<>(invertors, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete_invertor/{id}")
    public ResponseEntity<?> deleteInvertor(@PathVariable Long id) {
        try {
            Invertor invertor = invertorService.findById(id);
            if (invertor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invertor not found");
            }
            // Prima dată ștergem invertorul din Firebase
            firebaseService.deleteInvertor(id.toString());
            // Apoi ștergem invertorul din baza de date locală
            invertorService.deleteInvertor(id);
            return ResponseEntity.ok().body("Invertor deleted successfully from both MySQL and Firebase.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting invertor: " + e.getMessage());
        }
    }

    @GetMapping("/invertors/{id}") // Endpoint pentru a obține un invertor după ID
    public ResponseEntity<Invertor> getInvertorById(@PathVariable Long id) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentReference docRef = db.collection("invertors").document(id.toString());
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Invertor invertor = document.toObject(Invertor.class);
                return new ResponseEntity<>(invertor, HttpStatus.OK); // Returnăm un răspuns cu invertorul găsit și statusul HTTP 200 OK
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returnăm un răspuns cu statusul HTTP 404 Not Found dacă invertorul nu este găsit
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Returnăm un răspuns cu statusul HTTP 500 Internal Server Error în caz de eroare
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

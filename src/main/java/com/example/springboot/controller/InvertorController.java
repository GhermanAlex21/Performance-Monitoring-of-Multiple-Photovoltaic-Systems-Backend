package com.example.springboot.controller;

import com.example.springboot.model.*;
import com.example.springboot.service.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Autowired
    private SolarDataService solarDataService;  // Injectăm SolarDataService

    @PostMapping("/invertors")
    public ResponseEntity<?> addInvertor(@RequestBody InvertorRequest invertorRequest) {
        try {
            Long serieId = invertorRequest.getSerieId();
            Long marcaId = invertorRequest.getMarcaId();

            Serie serie = serieService.findById(serieId);
            if (serie == null) {
                serie = firebaseService.getSerieById(serieId.toString());
                if (serie != null) {
                    serie = serieService.saveSerie(serie);
                } else {
                    return ResponseEntity.badRequest().body("Serie does not exist in MySQL or Firebase");
                }
            }

            Marca marca = marcaService.findById(marcaId);
            if (marca == null) {
                marca = firebaseService.getMarcaById(marcaId.toString());
                if (marca != null) {
                    marca = marcaService.saveMarca(marca);
                } else {
                    return ResponseEntity.badRequest().body("Marca does not exist in MySQL or Firebase");
                }
            }

            Invertor invertor = new Invertor();
            invertor.setSerie(serie);
            invertor.setMarca(marca);
            invertor.setLatitude(invertorRequest.getLatitude());
            invertor.setLongitude(invertorRequest.getLongitude());
            invertor.setAzimut(invertorRequest.getAzimut());
            invertor.setPesId(invertorRequest.getPesId()); // Setăm pesId din request

            Invertor savedInvertor = invertorService.saveInvertor(invertor);
            String firebaseUpdateTime = firebaseService.addInvertor(savedInvertor);

            return ResponseEntity.ok("Invertor saved successfully in MySQL and Firebase at: " + firebaseUpdateTime);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/invertors/{id}/solar-data") // Endpoint nou pentru a obține datele solare pentru un anumit Invertor
    public ResponseEntity<List<SolarData>> getSolarDataForInvertor(@PathVariable Long id) {
        try {
            Invertor invertor = invertorService.findById(id);
            if (invertor != null) {
                List<SolarData> solarData = solarDataService.getSolarDataByPesId(invertor.getPesId());
                return new ResponseEntity<>(solarData, HttpStatus.OK);
            } else {
                // Returnează o listă goală cu status NOT FOUND
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Logica de error handling rămâne neschimbată
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
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

            // Încearcă să ștergi invertorul din Firebase indiferent dacă este găsit în MySQL
            firebaseService.deleteInvertor(id.toString());

            // Dacă invertorul este găsit în MySQL, șterge-l și de acolo
            if (invertor != null) {
                invertorService.deleteInvertor(id);
                return ResponseEntity.ok().body("Invertor deleted successfully from both MySQL and Firebase.");
            } else {
                // Invertorul nu este găsit în MySQL, dar a fost șters din Firebase
                return ResponseEntity.ok().body("Invertor deleted from Firebase, not found in MySQL.");
            }
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
            existingInvertor.setPesId(updatedInvertorRequest.getPesId());

            // Salvăm invertorul actualizat în baza de date
            Invertor updatedInvertor = invertorService.saveInvertor(existingInvertor);
            return new ResponseEntity<>(updatedInvertor, HttpStatus.OK); // Returnăm invertorul actualizat și statusul HTTP 200 OK
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Returnăm un răspuns cu statusul HTTP 404 Not Found dacă invertorul nu este găsit
        }
    }

    @GetMapping("/compare")
    public ResponseEntity<Map<String, Map<String, Object>>> compareInvertors(@RequestParam Long id1, @RequestParam Long id2) {
        try {
            Map<String, Map<String, Object>> comparisonData = invertorService.compareInvertors(id1, id2);
            return ResponseEntity.ok(comparisonData);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}

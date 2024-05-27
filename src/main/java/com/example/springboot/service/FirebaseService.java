package com.example.springboot.service;

import com.example.springboot.model.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class FirebaseService {
    private Firestore db = FirestoreClient.getFirestore();

    public String addUser(OurUser user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Firestore poate necesita un Map sau o clasă cu getteri/setteri publici și un constructor fără argumente.
        // Aici se face conversia, dacă e necesar, sau asigurați-vă că OurUser îndeplinește aceste cerințe.
        ApiFuture<WriteResult> result = db.collection("users").document(user.getId().toString()).set(user);
        return result.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru a confirma salvarea
    }
    public String addMarca(Marca marca) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Asigură-te că Marca are un ID și respectă cerințele Firebase pentru obiecte.
        ApiFuture<WriteResult> result = db.collection("marcas").document(marca.getId().toString()).set(marca);
        return result.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru a confirma salvarea
    }

    public String addSerie(Serie serie) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Asigură-te că Serie are un ID și respectă cerințele Firebase pentru obiecte.
        ApiFuture<WriteResult> result = db.collection("series").document(serie.getId().toString()).set(serie);
        return result.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru a confirma salvarea
    }

    public String addInvertor(Invertor invertor) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> result = db.collection("invertors").document(invertor.getId().toString()).set(invertor);
        return result.get().getUpdateTime().toString();
    }

    public String deleteUser(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection("users").document(userId).delete();
        return writeResult.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru confirmare
    }
    public String deleteSerie(String serieId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection("series").document(serieId).delete();
        return writeResult.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru confirmare
    }

    public String deleteMarca(String marcaId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection("marcas").document(marcaId).delete();
        return writeResult.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru confirmare
    }

    public String deleteInvertor(String invertorId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = db.collection("invertors").document(invertorId).delete();
        return writeResult.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru confirmare
    }

    public String updateUser(OurUser user) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Firestore poate necesita un Map sau o clasă cu getteri/setteri publici și un constructor fără argumente.
        // Aici se face conversia, dacă e necesar, sau asigurați-vă că OurUser îndeplinește aceste cerințe.
        ApiFuture<WriteResult> result = db.collection("users").document(user.getId().toString()).set(user);
        return result.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru a confirma salvarea
    }

    public String updateSerie(Serie serie) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> result = db.collection("series").document(serie.getId().toString()).set(serie);
        return result.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării pentru a confirma salvarea
    }
    public String updateInvertor(Invertor invertor) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> result = db.collection("invertors").document(invertor.getId().toString()).set(invertor);
        return result.get().getUpdateTime().toString(); // Returnează timestamp-ul actualizării
    }

    public String updateMarca(Marca marca) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        // Asigură-te că clasa Marca are gettere și settere pentru toate proprietățile care trebuie să fie serializate în Firestore.
        ApiFuture<WriteResult> writeResult = db.collection("marcas").document(marca.getId().toString()).set(marca);
        // Așteaptă finalizarea și returnează timpul de actualizare
        return writeResult.get().getUpdateTime().toString();
    }
    public Serie getSerieById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = db.collection("series").document(id).get().get();
        if (document.exists()) {
            return document.toObject(Serie.class);
        } else {
            return null;
        }
    }

    public List<Serie> getAllSeries() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection("series").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        return documents.stream().map(doc -> doc.toObject(Serie.class)).collect(Collectors.toList());
    }

    public Marca getMarcaById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = db.collection("marcas").document(id).get().get();
        if (document.exists()) {
            return document.toObject(Marca.class);
        } else {
            return null;
        }
    }

    public List<Marca> getAllMarcas() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection("marcas").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        return documents.stream().map(doc -> doc.toObject(Marca.class)).collect(Collectors.toList());
    }
    public OurUser getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentSnapshot document = db.collection("users").document(id).get().get();
        if (document.exists()) {
            return document.toObject(OurUser.class);
        } else {
            return null;
        }
    }

    public List<OurUser> getAllUsers() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        return documents.stream().map(doc -> doc.toObject(OurUser.class)).collect(Collectors.toList());
    }

    public boolean existsSerie(Long serieId) {
        try {
            DocumentSnapshot document = firestore.collection("serii").document(String.valueOf(serieId)).get().get();
            return document.exists();
        } catch (Exception e) {
            System.err.println("Error checking if serie exists: " + e.getMessage());
            return false;
        }
    }

    public boolean existsMarca(Long marcaId) {
        try {
            DocumentSnapshot document = firestore.collection("marci").document(String.valueOf(marcaId)).get().get();
            return document.exists();
        } catch (Exception e) {
            System.err.println("Error checking if marca exists: " + e.getMessage());
            return false;
        }
    }

    public void saveSolarData(SolarData solarData) {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference pesRef = db.collection("solarData").document(String.valueOf(solarData.getPesId()));

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("generation_mw", solarData.getGenerationMW());
        dataMap.put("datetime_gmt", solarData.getDatetimeGMT().toString());

        try {
            // Salvează data în subcolecția "records" a documentului corespunzător pes_id
            ApiFuture<WriteResult> writeResult = pesRef.collection("records")
                    .document(solarData.getDatetimeGMT().format(DateTimeFormatter.ISO_DATE_TIME))
                    .set(dataMap);

            // Așteaptă pentru a confirma operațiunea
            System.out.println("Data saved for PES ID " + solarData.getPesId() + " at " + writeResult.get().getUpdateTime());
        } catch (ExecutionException e) {
            System.err.println("Error saving data to Firebase: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore the interrupted status
            System.err.println("Thread interrupted while saving data to Firebase.");
        }
    }
    public void saveDailyAverage(int pesId, LocalDate date, double average) {
        db.collection("solarData").document(String.valueOf(pesId))
                .collection("dailyAverages").document(date.toString())
                .set(Map.of("average_generation_mw", average));
    }

    public void saveMonthlyAverage(int pesId, YearMonth month, double average) {
        db.collection("solarData").document(String.valueOf(pesId))
                .collection("monthlyAverages").document(month.toString())
                .set(Map.of("average_generation_mw", average));
    }
    public void saveWeeklyAverage(int pesId, int weekOfYear, double average, LocalDate startDate) {
        // Calculate the end date of the week
        LocalDate endDate = startDate.plusDays(6); // Assuming the week starts on the provided date and ends 6 days later

        // Use the startDate and endDate to create a range string
        String dateRange = startDate.toString() + " to " + endDate.toString();

        // Save the average under a document named after the date range
        db.collection("solarData").document(String.valueOf(pesId))
                .collection("weeklyAverages").document(dateRange)
                .set(Map.of("average_generation_mw", average));
    }
    public Map<LocalDate, Double> getDailyAverages(int pesId) throws ExecutionException, InterruptedException {
        Map<LocalDate, Double> averages = new HashMap<>();
        List<QueryDocumentSnapshot> documents = db.collection("solarData")
                .document(String.valueOf(pesId))
                .collection("dailyAverages")
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            averages.put(LocalDate.parse(document.getId()), document.getDouble("average_generation_mw"));
        }

        return averages;
    }

    public Map<String, Double> getWeeklyAverages(int pesId) throws ExecutionException, InterruptedException {
        Map<String, Double> averages = new HashMap<>();
        List<QueryDocumentSnapshot> documents = db.collection("solarData")
                .document(String.valueOf(pesId))
                .collection("weeklyAverages")
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            String dateRange = document.getId(); // The ID is now a date range
            double average = document.getDouble("average_generation_mw");
            averages.put(dateRange, average);
        }

        return averages;
    }

    public Map<YearMonth, Double> getMonthlyAverages(int pesId) throws ExecutionException, InterruptedException {
        Map<YearMonth, Double> averages = new HashMap<>();
        List<QueryDocumentSnapshot> documents = db.collection("solarData")
                .document(String.valueOf(pesId))
                .collection("monthlyAverages")
                .get()
                .get()
                .getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            averages.put(YearMonth.parse(document.getId()), document.getDouble("average_generation_mw"));
        }

        return averages;
    }
    public void saveComparisonData(int pesId, Map<String, Object> comparisonData) {
        db.collection("solarData").document(String.valueOf(pesId))
                .collection("comparisonData").document("latest")
                .set(comparisonData);
    }

    public Map<String, Object> getComparisonData(int pesId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection("solarData")
                .document(String.valueOf(pesId))
                .collection("comparisonData").document("latest")
                .get()
                .get();

        if (document.exists()) {
            return document.getData();
        } else {
            return new HashMap<>();
        }
    }

    private Firestore firestore;

    public FirebaseService() {
        this.firestore = FirestoreClient.getFirestore();
    }
}
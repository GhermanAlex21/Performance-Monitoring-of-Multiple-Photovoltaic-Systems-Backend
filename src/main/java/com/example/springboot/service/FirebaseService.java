package com.example.springboot.service;

import com.example.springboot.model.Invertor;
import com.example.springboot.model.Marca;
import com.example.springboot.model.OurUser;
import com.example.springboot.model.Serie;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FirebaseService {

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
}
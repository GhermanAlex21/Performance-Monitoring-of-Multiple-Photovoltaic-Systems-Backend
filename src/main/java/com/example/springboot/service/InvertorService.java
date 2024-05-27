package com.example.springboot.service;

import com.example.springboot.model.Invertor;
import com.example.springboot.model.Marca;
import com.example.springboot.repository.InvertorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class InvertorService {

    @Autowired
    private InvertorRepository invertorRepository;
    @Autowired
    private FirebaseService firebaseService;

    public Invertor saveInvertor(Invertor invertor) {
        return invertorRepository.save(invertor);
    }

    public List<Invertor> getAllInvertors() {
        return invertorRepository.findAll();
    }

    public void deleteInvertor(Long id) {
        invertorRepository.deleteById(id);
    }
    public Invertor findById(Long id) {
        Optional<Invertor> invertorOptional = invertorRepository.findById(id);
        return invertorOptional.orElse(null); // returnează invertorul dacă este găsit sau null dacă nu există
    }
    public List<Invertor> findBySerieId(Long serieId) {
        return invertorRepository.findBySerieId(serieId);
    }
    public List<Invertor> findByMarcaId(Long marcaId) {
        return invertorRepository.findByMarcaId(marcaId);
    }

    public Map<String, Map<String, Object>> compareInvertors(Long id1, Long id2) throws ExecutionException, InterruptedException {
        Invertor invertor1 = invertorRepository.findById(id1).orElseThrow(() -> new NoSuchElementException("Invertor not found"));
        Invertor invertor2 = invertorRepository.findById(id2).orElseThrow(() -> new NoSuchElementException("Invertor not found"));

        int pesId1 = invertor1.getPesId();
        int pesId2 = invertor2.getPesId();

        Map<String, Object> data1 = firebaseService.getComparisonData(pesId1);
        Map<String, Object> data2 = firebaseService.getComparisonData(pesId2);

        Map<String, Map<String, Object>> comparisonResult = new HashMap<>();
        comparisonResult.put("invertor1", data1);
        comparisonResult.put("invertor2", data2);

        return comparisonResult;
    }
}

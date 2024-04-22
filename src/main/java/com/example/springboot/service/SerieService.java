package com.example.springboot.service;

import com.example.springboot.model.Invertor;
import com.example.springboot.model.Marca;
import com.example.springboot.model.Serie;
import com.example.springboot.repository.InvertorRepository;
import com.example.springboot.repository.SerieRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private InvertorRepository invertorRepository;
    @Autowired
    private FirebaseService firebaseService;

    public Serie saveSerie(Serie serie) {
        return serieRepository.save(serie);
    }

    public List<Serie> getAllSeries() {
        return serieRepository.findAll();
    }

    public Serie findById(Long id) {
        Optional<Serie> optionalSerie = serieRepository.findById(id);
        return optionalSerie.orElse(null);
    }

    public void deleteSerie(Long id) throws ExecutionException, InterruptedException {
        Optional<Serie> serieOptional = serieRepository.findById(id);
        if (serieOptional.isPresent()) {
            Serie serie = serieOptional.get();

            // Șterge toți invertorii legați de această serie din Firebase
            List<Invertor> invertors = invertorRepository.findBySerieId(id);
            for (Invertor invertor : invertors) {
                firebaseService.deleteInvertor(invertor.getId().toString()); // Asigură-te că metoda aceasta există în firebaseService
            }

            // Șterge seria din Firebase
            firebaseService.deleteSerie(id.toString());

            // Șterge seria și invertorii din baza de date locală
            serieRepository.deleteById(id);  // Aceasta va șterge și invertorii datorită setării CascadeType.ALL
        } else {
            throw new EntityNotFoundException("Serie not found with ID: " + id);
        }
    }

    public Serie updateSerie(Long id, @Valid Serie serie) throws ChangeSetPersister.NotFoundException {
        Optional<Serie> existingSerieOptional = serieRepository.findById(id);
        if (existingSerieOptional.isPresent()) {
            Serie existingSerie = existingSerieOptional.get();
            existingSerie.setNume(serie.getNume());
            return serieRepository.save(existingSerie);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }
}

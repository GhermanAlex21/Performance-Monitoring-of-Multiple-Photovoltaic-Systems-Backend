package com.example.springboot.service;

import com.example.springboot.model.Marca;
import com.example.springboot.model.Serie;
import com.example.springboot.repository.SerieRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SerieService {

    @Autowired
    private SerieRepository serieRepository;

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

    public void deleteSerie(Long id) {
        serieRepository.deleteById(id);
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

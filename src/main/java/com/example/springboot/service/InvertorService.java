package com.example.springboot.service;

import com.example.springboot.model.Invertor;
import com.example.springboot.model.Marca;
import com.example.springboot.repository.InvertorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvertorService {

    @Autowired
    private InvertorRepository invertorRepository;

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
}

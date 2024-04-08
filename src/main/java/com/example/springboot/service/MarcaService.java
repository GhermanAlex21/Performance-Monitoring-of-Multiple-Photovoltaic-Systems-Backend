package com.example.springboot.service;

import com.example.springboot.model.Marca;
import com.example.springboot.repository.MarcaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;

    public Marca saveMarca(Marca marca) {
        return marcaRepository.save(marca);
    }

    public List<Marca> getAllMarcas() {
        return marcaRepository.findAll();
    }
    public Marca findById(Long id) {
        Optional<Marca> optionalMarca = marcaRepository.findById(id);
        return optionalMarca.orElse(null);
    }
    public void deleteMarca(Long id) {
        marcaRepository.deleteById(id);
    }

    public Marca updateMarca(Long id, @Valid Marca marca) throws ChangeSetPersister.NotFoundException {
        Optional<Marca> existingMarcaOptional = marcaRepository.findById(id);
        if (existingMarcaOptional.isPresent()) {
            Marca existingMarca = existingMarcaOptional.get();
            existingMarca.setNume(marca.getNume());
            // Adăugați aici și alte atribute pe care doriți să le actualizați
            return marcaRepository.save(existingMarca);
        } else {
            throw new ChangeSetPersister.NotFoundException();
        }
    }




}

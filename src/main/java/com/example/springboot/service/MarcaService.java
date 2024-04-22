package com.example.springboot.service;

import com.example.springboot.model.Invertor;
import com.example.springboot.model.Marca;
import com.example.springboot.repository.InvertorRepository;
import com.example.springboot.repository.MarcaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class MarcaService {

    @Autowired
    private MarcaRepository marcaRepository;
    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private InvertorRepository invertorRepository;

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
    public void deleteMarca(Long id) throws ExecutionException, InterruptedException {
        Optional<Marca> marcaOptional = marcaRepository.findById(id);
        if (marcaOptional.isPresent()) {
            Marca marca = marcaOptional.get();

            // Șterge toți invertorii legați de această marcă din Firebase
            List<Invertor> invertors = invertorRepository.findByMarcaId(id);
            for (Invertor invertor : invertors) {
                firebaseService.deleteInvertor(invertor.getId().toString()); // Asigură-te că metoda aceasta există în firebaseService
            }

            // Șterge marca din Firebase
            firebaseService.deleteMarca(id.toString());

            // Șterge marca și invertorii din baza de date locală
            marcaRepository.deleteById(id);  // Aceasta va șterge și invertorii datorită setării CascadeType.ALL
        } else {
            throw new EntityNotFoundException("Marca not found with ID: " + id);
        }
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

package com.example.springboot.controller;

import com.example.springboot.model.Marca;
import com.example.springboot.model.Serie;
import com.example.springboot.service.MarcaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

public class MarcaController {

    @Autowired
    private MarcaService marcaService;

    @PostMapping("/adauga_marca")
    public Marca addMarca(@RequestBody Marca marca) {
        return marcaService.saveMarca(marca);
    }
    @GetMapping("/get_marca") // Endpoint pentru a obține toate mărcile
    public List<Marca> getAllMarcas() {
        return marcaService.getAllMarcas();
    }
    @DeleteMapping("/delete_marca/{id}")
    public void deleteMarca(@PathVariable Long id) {
        marcaService.deleteMarca(id);
    }
    @GetMapping("/marca/{id}")
    public ResponseEntity<Marca> getMarcaById(@PathVariable Long id) {
        Marca marca = marcaService.findById(id);
        if (marca != null) {
            return ResponseEntity.ok(marca);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PutMapping("/update_marca/{id}")
    public ResponseEntity<Marca> updateMarca(@PathVariable Long id, @RequestBody Marca marca) throws ChangeSetPersister.NotFoundException {
        Marca updatedMarca = marcaService.updateMarca(id, marca);
        return ResponseEntity.ok(updatedMarca);
    }
}

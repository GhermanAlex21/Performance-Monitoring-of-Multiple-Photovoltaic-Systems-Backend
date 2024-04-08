package com.example.springboot.controller;

import com.example.springboot.model.Marca;
import com.example.springboot.model.Serie;
import com.example.springboot.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class SerieController {

    @Autowired
    private SerieService serieService;

    @PostMapping("/series")
    public Serie addSerie(@RequestBody Serie serie) {
        return serieService.saveSerie(serie);
    }

    @GetMapping("/get_serii") // Endpoint pentru a obține toate mărcile
    public List<Serie> getAllSeries() {
        return serieService.getAllSeries();
    }

    @DeleteMapping("/delete_serie/{id}")
    public void deleteSerie(@PathVariable Long id) {
        serieService.deleteSerie(id);
    }

    @GetMapping("/serie/{id}")
    public ResponseEntity<Serie> getSerieById(@PathVariable Long id) {
        Serie serie = serieService.findById(id);
        if (serie != null) {
            return ResponseEntity.ok(serie);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PutMapping("/update_serie/{id}")
    public ResponseEntity<Serie> updateSerie(@PathVariable Long id, @RequestBody Serie serie) throws ChangeSetPersister.NotFoundException {
        Serie updatedSerie = serieService.updateSerie(id, serie);
        return ResponseEntity.ok(updatedSerie);
    }
}

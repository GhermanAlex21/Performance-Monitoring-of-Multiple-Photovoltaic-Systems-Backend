package com.example.springboot.service;

import com.example.springboot.model.Invertor;
import com.example.springboot.model.Marca;
import com.example.springboot.model.OurUser;
import com.example.springboot.model.Serie;
import com.example.springboot.repository.InvertorRepository;
import com.example.springboot.repository.MarcaRepository;
import com.example.springboot.repository.OurUserRepo;
import com.example.springboot.repository.SerieRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataMigrationService {

    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private MarcaRepository marcaRepo;
    @Autowired
    private SerieRepository serieRepository;
    @Autowired
    private InvertorRepository invertorRepository;

    @Autowired
    private FirebaseService firebaseService;

    @PostConstruct
    public void migrateData() {
        migrateUsers();
        migrateMarcas();
        migrateSeries();
        migrateInvertors();
    }

    private void migrateUsers() {
        try {
            List<OurUser> users = ourUserRepo.findAll();
            for (OurUser user : users) {
                firebaseService.addUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void migrateMarcas() {
        try {
            List<Marca> marcas = marcaRepo.findAll();
            for (Marca marca : marcas) {
                firebaseService.addMarca(marca);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void migrateSeries() {
        try {
            List<Serie> series = serieRepository.findAll();
            for (Serie serie : series) {
                firebaseService.addSerie(serie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void migrateInvertors() {
        try {
            List<Invertor> invertors = invertorRepository.findAll();
            for (Invertor invertor : invertors) {
                firebaseService.addInvertor(invertor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
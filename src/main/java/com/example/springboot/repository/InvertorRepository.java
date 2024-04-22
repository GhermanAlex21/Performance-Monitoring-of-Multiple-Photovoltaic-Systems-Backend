package com.example.springboot.repository;

import com.example.springboot.model.Invertor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvertorRepository extends JpaRepository<Invertor, Long> {
    List<Invertor> findBySerieId(Long serieId);
    List<Invertor> findByMarcaId(Long marcaId);

}
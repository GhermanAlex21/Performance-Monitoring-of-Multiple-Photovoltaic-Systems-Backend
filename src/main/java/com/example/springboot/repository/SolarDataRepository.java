package com.example.springboot.repository;

import com.example.springboot.model.SolarData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SolarDataRepository extends JpaRepository<SolarData, Long> {
    boolean existsByDatetimeGMTAndPesId(LocalDateTime datetimeGMT, Integer pesId);
    List<SolarData> findByPesId(Integer pesId);
}

package com.example.springboot.controller;

import com.example.springboot.service.FirebaseService;
import com.example.springboot.service.SolarDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class SolarDataController {

    @Autowired
    private SolarDataService solarDataService;
    @Autowired
    private FirebaseService firebaseService;

    @GetMapping("/daily/{pesId}")
    public ResponseEntity<?> getDailyAverages(@PathVariable int pesId) {
        try {
            var averages = firebaseService.getDailyAverages(pesId);
            return ResponseEntity.ok(averages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching daily averages: " + e.getMessage());
        }
    }

    @GetMapping("/weekly/{pesId}")
    public ResponseEntity<?> getWeeklyAverages(@PathVariable int pesId) {
        try {
            Map<String, Double> averages = firebaseService.getWeeklyAverages(pesId);
            return ResponseEntity.ok(averages);
        } catch (Exception e) {
            e.printStackTrace();  // This will print the stack trace to your log
            return ResponseEntity.internalServerError().body("Error fetching weekly averages: " + e.getMessage());
        }
    }

    @GetMapping("/monthly/{pesId}")
    public ResponseEntity<?> getMonthlyAverages(@PathVariable int pesId) {
        try {
            var averages = firebaseService.getMonthlyAverages(pesId);
            return ResponseEntity.ok(averages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching monthly averages: " + e.getMessage());
        }
    }
}
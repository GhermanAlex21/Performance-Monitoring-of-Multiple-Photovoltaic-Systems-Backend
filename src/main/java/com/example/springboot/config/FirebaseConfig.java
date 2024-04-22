package com.example.springboot.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebaseApp() {
        try {
            InputStream serviceAccount = new ClassPathResource(
                    "proiect-licenta-59247-firebase-adminsdk-mu4j0-78d03f4e86.json").getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // Verifică dacă Firebase nu a fost inițializat deja
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Nu s-a putut inițializa Firebase", e);
        }
    }
}
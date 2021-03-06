package com.thirdcc.webapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
public class FirebaseAppConfig {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Bean
    public Firestore firebaseInit(ResourceLoader resourceLoader) throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) return FirestoreClient.getFirestore();
        LOGGER.debug("Init FirebaseApp");
        Resource resource = resourceLoader.getResource("classpath:ccclubmanagement-firebase-adminsdk-h8s4n-bfd52e02b9.json");
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
            .setProjectId("ccclubmanagement")
            .build();
        FirebaseApp.initializeApp(options);
        Firestore db = FirestoreClient.getFirestore();
        return db;
    }
}

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
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

@Configuration
public class FirebaseAppConfig {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Bean
    public Firestore firebaseInit(ResourceLoader resourceLoader, ApplicationProperties applicationProperties) throws IOException {
        if (!applicationProperties.isShouldInitFirebase()) return null;
        if (!FirebaseApp.getApps().isEmpty()) return FirestoreClient.getFirestore();
        LOGGER.debug("Init FirebaseApp");
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .setProjectId("ccclubmanagement")
            .build();
        FirebaseApp.initializeApp(options);
        Firestore db = FirestoreClient.getFirestore();
        return db;
    }
}

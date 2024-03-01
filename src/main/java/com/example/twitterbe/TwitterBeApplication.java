package com.example.twitterbe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class TwitterBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterBeApplication.class, args);
    }
    @Bean
    @Primary
    public FirebaseApp firebaseInitialization() throws IOException {
        Resource resource = new ClassPathResource("twitter-a10b3-firebase-adminsdk-ryk1o-4ea439ec20.json");
        FileInputStream serviceAccount = new FileInputStream(resource.getFile());
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return null; // Hoặc trả về FirebaseApp nếu cần
    }
    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.build();
    }


}

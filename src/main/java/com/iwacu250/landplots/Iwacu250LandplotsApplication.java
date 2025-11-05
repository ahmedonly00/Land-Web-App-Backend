package com.iwacu250.landplots;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Iwacu250LandplotsApplication {
    
    @PostConstruct
    public void init() {
        // Load .env file
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        // Log environment variables (without sensitive data)
        System.out.println("=== Environment Variables ===");
        System.out.println("DB_URL: " + (dotenv.get("DB_URL") != null ? "[SET]" : "null"));
        System.out.println("DB_USER: " + (dotenv.get("DB_USER") != null ? "[SET]" : "null"));
        System.out.println("DB_PASSWORD: " + (dotenv.get("DB_PASSWORD") != null ? "[SET]" : "null"));
        
        // Set system properties from .env file
        dotenv.entries().forEach(entry -> 
            System.setProperty(entry.getKey(), entry.getValue())
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(Iwacu250LandplotsApplication.class, args);
    }
}

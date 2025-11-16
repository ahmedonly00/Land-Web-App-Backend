package com.iwacu250.landplots.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EnvironmentConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    
    private final Dotenv dotenv;
    
    public EnvironmentConfig() {
        this.dotenv = loadEnvironment();
    }
    
    @PostConstruct
    public void init() {
        System.out.println("=== ENVIRONMENT CONFIG STARTING ===");
        logger.info("Loading environment variables from .env file...");

        // Load database properties from .env if they exist
        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USERNAME");
        String dbPass = dotenv.get("DB_PASSWORD");
        
        if (dbUrl != null) {
            System.setProperty("spring.datasource.url", dbUrl);
            logger.info("Database URL loaded from .env");
        }
        if (dbUser != null) {
            System.setProperty("spring.datasource.username", dbUser);
            logger.info("Database username loaded from .env");
        }
        if (dbPass != null) {
            System.setProperty("spring.datasource.password", dbPass);
            logger.info("Database password loaded from .env");
        }
        
        // Load JWT properties from .env if they exist
        String jwtSecret = dotenv.get("JWT_SECRET");
        String jwtExpiration = dotenv.get("JWT_EXPIRATION");
        String jwtRefreshExpiration = dotenv.get("JWT_REFRESH_TOKEN_EXPIRATION");
        String jwtPasswordResetExpiration = dotenv.get("JWT_PASSWORD_RESET_EXPIRATION");
        
        logger.info("JWT_SECRET from .env: {}", jwtSecret != null ? "[FOUND]" : "[NOT FOUND]");
        if (jwtSecret != null) {
            logger.info("JWT_SECRET length: {} characters", jwtSecret.length());
            System.setProperty("jwt.secret", jwtSecret);
            logger.info("JWT secret loaded from .env");
        } else {
            logger.warn("JWT_SECRET not found in .env file - using default from application.properties");
        }
        if (jwtExpiration != null) {
            System.setProperty("jwt.expiration", jwtExpiration);
        }
        if (jwtRefreshExpiration != null) {
            System.setProperty("jwt.refresh-token.expiration", jwtRefreshExpiration);
        }
        if (jwtPasswordResetExpiration != null) {
            System.setProperty("jwt.password-reset.expiration", jwtPasswordResetExpiration);
        }
        
        logger.info("Environment configuration completed");
    }
    
    private Dotenv loadEnvironment() {
        logger.info("Loading .env file from: {}", System.getProperty("user.dir"));
        try {
            return Dotenv.configure()
                .filename(".env")
                .ignoreIfMissing()
                .load();
        } catch (Exception e) {
            logger.error("Failed to load .env file", e);
            throw new IllegalStateException("Failed to load environment variables", e);
        }
    }
    
}

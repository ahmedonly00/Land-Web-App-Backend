package com.iwacu250.landplots.config;

import com.iwacu250.landplots.entity.ERole;
import com.iwacu250.landplots.entity.Role;
import com.iwacu250.landplots.entity.User;
import com.iwacu250.landplots.repository.RoleRepository;
import com.iwacu250.landplots.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@Profile("!prod") // Only run this in non-production environments
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initAdminUser(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            System.out.println("\n=== Starting Admin User Initialization ===");
            
            // Ensure admin role exists
            System.out.println("Ensuring admin role exists...");
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));
            
            // Check if admin user already exists
            if (!userRepository.existsByUsername("admin")) {
                System.out.println("Creating default admin user...");
                // Create admin user
                User admin = new User(
                    "admin",
                    "admin@example.com",
                    passwordEncoder.encode("admin123"),
                    "Admin User",
                    "+250788888888",
                    "Kigali, Rwanda"
                );
                admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
                userRepository.save(admin);
                System.out.println("Default admin user created with username: admin and password: admin123");
            } else {
                System.out.println("Admin user already exists, skipping creation.");
            }
            
            System.out.println("=== Admin User Initialization Complete ===\n");
        };
    }
}

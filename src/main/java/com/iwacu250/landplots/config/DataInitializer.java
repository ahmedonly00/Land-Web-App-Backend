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
@Profile("dev")
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
            
            // Check if admin user already exists by username or email
            String adminUsername = "karim";
            String adminEmail = "karimkanakuze2050@gmail.com";
            
            boolean userExists = userRepository.existsByUsername(adminUsername) || 
                               userRepository.existsByEmail(adminEmail);
            
            if (!userExists) {
                System.out.println("Creating default admin user...");
                // Create admin user
                User admin = new User(
                    adminUsername,
                    adminEmail,
                    passwordEncoder.encode("admin123"),
                    "Admin User",
                    "+250780314239",
                    "Kigali, Rwanda"
                );
                admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
                try {
                    userRepository.save(admin);
                    System.out.println("Default admin user created with username: " + adminUsername + " and password: admin123");
                } catch (Exception e) {
                    System.err.println("Error creating admin user: " + e.getMessage());
                }
            } else {
                System.out.println("Admin user with username 'karim' or email 'karimkanakuze2050@gmail.com' already exists, skipping creation.");
            }
            
            System.out.println("=== Admin User Initialization Complete ===\n");
        };
    }
}

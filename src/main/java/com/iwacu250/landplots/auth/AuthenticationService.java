package com.iwacu250.landplots.auth;

import com.iwacu250.landplots.dto.auth.LoginRequest;
import com.iwacu250.landplots.dto.auth.LoginResponse;
import com.iwacu250.landplots.dto.auth.RegisterRequest;
import com.iwacu250.landplots.dto.auth.ResetPasswordRequest;
import com.iwacu250.landplots.entity.ERole;
import com.iwacu250.landplots.entity.Role;
import com.iwacu250.landplots.entity.User;
import com.iwacu250.landplots.repository.RoleRepository;
import com.iwacu250.landplots.repository.UserRepository;
import com.iwacu250.landplots.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse authenticate(LoginRequest request) {
        log.info("Starting authentication for user: {}", request != null ? request.getUsername() : "null");
        
        if (request == null) {
            log.error("Login request is null");
            throw new IllegalArgumentException("Login request cannot be null");
        }
        
        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is empty");
            throw new IllegalArgumentException("Username is required");
        }
        
        try {
            log.debug("Looking up user with username: {}", username);
            
            // Find user by username
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                log.warn("User not found with username: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            
            User user = userOpt.get();
            log.debug("Found user: {} with ID: {}", user.getUsername(), user.getId());

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                log.warn("Invalid password for user: {}", username);
                throw new BadCredentialsException("Invalid username or password");
            }

            // Check if account is active
            if (!user.isActive()) {
                log.warn("Inactive account login attempt: {}", username);
                throw new RuntimeException("Account is not active. Please contact support.");
            }
            
            // Check if user has admin role - FIXED
            boolean isAdmin = user.getRoles() != null && 
                user.getRoles().stream()
                    .filter(Objects::nonNull)
                    .map(Role::getName)
                    .anyMatch(ERole.ROLE_ADMIN::equals);
                
            if (!isAdmin) {
                log.warn("Non-admin login attempt: {}", username);
                throw new BadCredentialsException("Access denied. Admin privileges required.");
            }

            log.debug("Generating JWT token for user: {}", username);
            String jwtToken = jwtTokenProvider.generateToken(user);

            // Build response - create a safe copy of roles for serialization
            Set<Role> rolesForResponse = new HashSet<>(user.getRoles());
            
            LoginResponse response = LoginResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(rolesForResponse)
                .build();
                
            log.info("Successfully authenticated user: {}", username);
            return response;

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.warn("Authentication failed for user: {} - {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: " + username, e);
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void registerAdmin(RegisterRequest request) {
        // Only allow one admin user in the system
        if (userRepository.count() > 0) {
            throw new RuntimeException("Admin user already exists. Cannot register new admin.");
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Get or create ADMIN role
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
            .orElseGet(() -> {
                Role newRole = new Role(ERole.ROLE_ADMIN);
                return roleRepository.save(newRole);
            });

        // Create new admin user
        User user = new User(
            request.getUsername(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            request.getFullName(),
            request.getPhone(),
            request.getAddress()
        );
        
        // Set admin role
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);
        
        userRepository.save(user);
        log.info("Admin user registered successfully: {}", request.getUsername());
    }

    public void logout(String token) {
        log.info("User logged out successfully");
    }

    @Transactional(readOnly = true)
    public String refreshToken(String token) {
        // Extract username from token
        String username = jwtTokenProvider.extractUsername(token);
        if (username == null) {
            throw new BadCredentialsException("Invalid token");
        }

        // Load user details
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!user.isActive()) {
            throw new RuntimeException("User account is not active");
        }

        // Generate new token
        return jwtTokenProvider.generateToken(user);
    }
    
    @Transactional(readOnly = true)
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!user.isActive()) {
            throw new RuntimeException("User account is not active");
        }

        // Generate reset token
        String resetToken = jwtTokenProvider.generatePasswordResetToken(user);

        log.info("Password reset token for {}: {}", email, resetToken);
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Validate token
        if (!jwtTokenProvider.validateToken(request.getToken())) {
            throw new RuntimeException("Invalid or expired token");
        }

        // Extract username from token
        String username = jwtTokenProvider.extractUsername(request.getToken());
        if (username == null) {
            throw new BadCredentialsException("Invalid token");
        }

        // Find user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User account is not active");
        }

        // Validate password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password reset successfully for user: {}", username);
    }
}
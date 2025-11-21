package com.iwacu250.landplots.auth;

import com.iwacu250.landplots.dto.auth.LoginRequest;
import com.iwacu250.landplots.dto.auth.LoginResponse;
import com.iwacu250.landplots.dto.auth.RegisterRequest;
import com.iwacu250.landplots.dto.auth.ResetPasswordRequest;
import com.iwacu250.landplots.entity.Role;
import com.iwacu250.landplots.entity.User;
import com.iwacu250.landplots.repository.RoleRepository;
import com.iwacu250.landplots.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"https://iwacu250.com", "https://www.iwacu250.com"})
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthenticationController(AuthenticationService authService, UserRepository userRepository, RoleRepository roleRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Login request cannot be null");
        }
        
        String username = request.getUsername() != null ? request.getUsername() : "[no username provided]";
        log.info("Admin login attempt for username: {}", username);
        
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errorMessage = "Invalid request";
            var fieldError = bindingResult.getFieldError();
            if (fieldError != null && fieldError.getDefaultMessage() != null) {
                errorMessage = fieldError.getDefaultMessage();
            }
            log.warn("Validation errors in login request: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(errorMessage);
        }
        
        try {
            // Check if admin user exists
            if (userRepository.count() == 0) {
                return ResponseEntity.status(403).body("No admin user exists. Please register an admin first.");
            }
            
            LoginResponse response = authService.authenticate(request);
            log.info("Admin login successful: {}", username);
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for admin: {}", username);
            return ResponseEntity.status(401).body("Invalid username or password");
        } catch (Exception e) {
            log.error("Login failed for admin: {}", username, e);
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        log.info("Received admin registration request for: {}", request.getUsername());
        
        if (bindingResult.hasErrors()) {
            String errorMessage = "Invalid registration data";
            var fieldError = bindingResult.getFieldError();
            if (fieldError != null && fieldError.getDefaultMessage() != null) {
                errorMessage = fieldError.getDefaultMessage();
            }
            log.warn("Validation errors in admin registration: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(errorMessage);
        }
        
        try {
            authService.registerAdmin(request);
            log.info("Admin user registered successfully: {}", request.getUsername());
            return ResponseEntity.ok("Admin user registered successfully");
        } catch (Exception e) {
            log.error("Admin registration failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        log.info("Password reset requested for email: {}", email);
        
        try {
            authService.requestPasswordReset(email);
            return ResponseEntity.ok("Password reset instructions have been sent to your email");
        } catch (Exception e) {
            log.error("Password reset request failed for email: {}", email, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Processing password reset request");
        
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok("Password has been reset successfully");
        } catch (Exception e) {
            log.error("Error resetting password", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String token) {
        log.info("Logging out user");
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String token) {
        log.info("Refreshing token");
        try {
            String newToken = authService.refreshToken(token);
            return ResponseEntity.ok(newToken);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/test-db")
    public ResponseEntity<?> testDatabase() {
        try {
            // Test user repository
            long userCount = userRepository.count();
            List<User> allUsers = userRepository.findAll();
            
            // Test role repository
            long roleCount = roleRepository.count();
            List<Role> allRoles = roleRepository.findAll();
            
            // Build response
            StringBuilder response = new StringBuilder();
            response.append("Database connection successful!\n");
            response.append("Total users: ").append(userCount).append("\n");
            response.append("Total roles: ").append(roleCount).append("\n");
            
            if (!allUsers.isEmpty()) {
                response.append("\nUsers:\n");
                for (User user : allUsers) {
                    response.append("- ID: ").append(user.getId())
                           .append(", Username: ").append(user.getUsername())
                           .append(", Email: ").append(user.getEmail())
                           .append(", Active: ").append(user.isActive())
                           .append("\n");
                }
            }
            
            if (!allRoles.isEmpty()) {
                response.append("\nRoles:\n");
                for (Role role : allRoles) {
                    response.append("- ID: ").append(role.getId())
                           .append(", Name: ").append(role.getName())
                           .append("\n");
                }
            }
            
            return ResponseEntity.ok(response.toString());
            
        } catch (Exception e) {
            log.error("Database test failed", e);
            return ResponseEntity.status(500).body("Database test failed: " + e.getMessage());
        }
    }
}

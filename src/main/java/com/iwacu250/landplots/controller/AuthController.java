package com.iwacu250.landplots.controller;

import com.iwacu250.landplots.dto.LoginRequest;
import com.iwacu250.landplots.dto.RegisterRequest;
import com.iwacu250.landplots.dto.AuthResponse;
import com.iwacu250.landplots.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication related requests
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}", maxAge = 3600)
@Tag(name = "Authentication", description = "Authentication and user management APIs")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate a user and return JWT token
     *
     * @param loginRequest User login credentials
     * @return JWT token and user details
     */
    @Operation(summary = "Authenticate user", description = "Authenticate user and return JWT token")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully authenticated",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Register a new user account
     *
     * @param registerRequest User registration details
     * @return JWT token and user details
     */
    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input or user already exists"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout the current user (invalidate token on client side)
     *
     * @return Success message
     */
    @Operation(summary = "Logout user", description = "Invalidate JWT token (client-side)")
    @ApiResponse(
        responseCode = "200", 
        description = "Successfully logged out"
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Note: Token invalidation is handled client-side
        return ResponseEntity.ok().build();
    }
}

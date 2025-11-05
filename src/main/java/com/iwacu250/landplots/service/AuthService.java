package com.iwacu250.landplots.service;

import com.iwacu250.landplots.dto.AuthResponse;
import com.iwacu250.landplots.dto.LoginRequest;
import com.iwacu250.landplots.dto.RegisterRequest;
import com.iwacu250.landplots.model.ERole;
import com.iwacu250.landplots.model.Role;
import com.iwacu250.landplots.model.User;
import com.iwacu250.landplots.exception.ResourceAlreadyExistsException;
import com.iwacu250.landplots.exception.ResourceNotFoundException;
import com.iwacu250.landplots.repository.RoleRepository;
import com.iwacu250.landplots.repository.UserRepository;
import com.iwacu250.landplots.security.JwtTokenProvider;
import com.iwacu250.landplots.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private static final ERole DEFAULT_ROLE = ERole.ROLE_USER;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                      UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use!");
        }

        // Create new user's account
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFullName(),
                registerRequest.getPhoneNumber(),
                registerRequest.getAddress()
        );

        // Set default role
        Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseGet(() -> {
                    // Create the role if it doesn't exist
                    Role newRole = new Role(DEFAULT_ROLE);
                    return roleRepository.save(newRole);
                });
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Save user to database
        user = userRepository.save(user);

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                DEFAULT_ROLE.replace("ROLE_", "")
        );
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .orElse("");

        return new AuthResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role
        );
    }

    @Transactional
    public User registerAdmin(String username, String email, String password, String fullName) {
        if (userRepository.existsByUsername(username)) {
            throw new ResourceAlreadyExistsException("User", "username", username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("User", "email", email);
        }

        // Create new admin user
        User admin = new User(
            username,
            email,
            passwordEncoder.encode(password),
            fullName,
            null,  // phoneNumber
            null   // address
        );
        
        // Get or create admin role
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseGet(() -> {
                    Role newRole = new Role(ERole.ROLE_ADMIN);
                    return roleRepository.save(newRole);
                });
        
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        admin.setRoles(roles);
        
        return userRepository.save(admin);
    }
}

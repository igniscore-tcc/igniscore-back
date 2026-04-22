package com.igniscore.api.controller;

import com.igniscore.api.dto.AutDTO;
import com.igniscore.api.dto.RegisterDTO;
import com.igniscore.api.dto.LoginResponseDTO;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.service.JwtService;
import jakarta.validation.Valid;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling authentication-related HTTP requests.
 * This class provides endpoints for user login and registration.
 * It integrates with Spring Security for authentication and uses
 * JWT (JSON Web Token) for session management.
 */
@RestController
@RequestMapping("auth")
@SuppressWarnings("unused")
public class AuthController {

    /**
     * Manages authentication operations.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Repository responsible for user data persistence.
     */
    private final UserRepository repository;

    /**
     * Service responsible for generating JWT tokens.
     */
    private final JwtService jwtService;

    /**
     * Constructor for AuthController.
     *
     * @param authenticationManager the authentication manager
     * @param repository            the user repository
     * @param jwtService            the JWT service
     */
    @SuppressWarnings("unused")
    public AuthController(AuthenticationManager authenticationManager, UserRepository repository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint used to authenticate a user and generate a JWT token.
     * This method validates the provided credentials, authenticates the user
     * using Spring Security, and returns a JWT token if authentication succeeds.
     *
     * @param data the authentication data containing email and password
     * @return a ResponseEntity containing the generated JWT token
     * @throws RuntimeException if authentication fails or the principal is invalid
     */
    @PostMapping("/login")
    @SuppressWarnings("unused")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid @NonNull AutDTO data) {
        var usernamepassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamepassword);

        var principal = auth.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new RuntimeException("User authentication failed");
        }

        var token = jwtService.generateJwt(user);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    /**
     * Endpoint used to register a new user.
     * This method checks if the email is already in use, encrypts the password,
     * creates a new User entity, and persists it in the database.
     *
     * @param data the registration data containing user information
     * @return a ResponseEntity indicating success or failure
     */
    @PostMapping("/register")
    @SuppressWarnings("unused")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterDTO data) {
        if(this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(data.role());

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
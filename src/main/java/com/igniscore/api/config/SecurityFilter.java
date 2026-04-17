package com.igniscore.api.config;

import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security filter responsible for intercepting HTTP requests and handling JWT-based authentication.
 * This filter executes once per request and is responsible for:
 * - Extracting the JWT token from the Authorization header
 * - Validating the token
 * - Retrieving the associated user
 * - Setting the authentication context in Spring Security
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    /**
     * Service responsible for JWT operations such as validation and data extraction.
     */
    private final JwtService service;

    /**
     * Repository used to retrieve user data from the database.
     */
    private final UserRepository repository;

    /**
     * Constructor for SecurityFilter.
     *
     * @param service     the JWT service
     * @param repository  the user repository
     */
    public SecurityFilter(JwtService service, UserRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    /**
     * Core filter method that processes each incoming HTTP request.
     * This method:
     * - Extracts the JWT token from the request
     * - Validates the token
     * - Retrieves user information from the database
     * - Creates an authentication object and sets it in the security context
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException in case of servlet errors
     * @throws IOException      in case of I/O errors
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            String email = service.validateToken(token);
            Integer companyId = service.getCompanyIdFromToken(token);

            if (email != null) {
                var user = repository.findByEmail(email);
                if (user != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(companyId);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     * The expected format is: Bearer <token>".
     *
     * @param request the HTTP request
     * @return the extracted token, or null if not present or invalid
     */
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
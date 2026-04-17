package com.igniscore.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.igniscore.api.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Service responsible for generating and validating JSON Web Tokens (JWT).
 *
 * <p>This class encapsulates all JWT-related operations including:
 * <ul>
 *     <li>Token generation with user claims</li>
 *     <li>Token validation and subject extraction</li>
 *     <li>Custom claim retrieval (e.g., companyId)</li>
 * </ul>
 *
 * <p>Security notes:
 * <ul>
 *     <li>Uses HMAC256 symmetric signing</li>
 *     <li>Relies on a secret key injected via application configuration</li>
 *     <li>Enforces issuer validation</li>
 * </ul>
 */
@Service
public class JwtService {

    /**
     * Secret key used to sign and verify JWT tokens.
     * <p>Injected from application configuration (application.properties / env).
     */
    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Generates a signed JWT token for a given user.
     *
     * <p>The token includes:
     * <ul>
     *     <li>Subject: user email</li>
     *     <li>Custom claim: companyId</li>
     *     <li>Issuer: igniscore</li>
     *     <li>Expiration time</li>
     * </ul>
     *
     * @param user authenticated user
     * @return signed JWT token
     *
     * @throws RuntimeException if token creation fails
     */
    public String generateJwt(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("igniscore")
                    .withSubject(user.getEmail())
                    .withClaim(
                            "companyId",
                            user.getCompany() != null ? user.getCompany().getId() : null
                    )
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    /**
     * Validates a JWT token and extracts its subject (user identifier).
     *
     * @param token JWT token
     * @return subject (user email) if valid, empty string otherwise
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("igniscore")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    /**
     * Extracts the company ID from a JWT token.
     *
     * @param token JWT token
     * @return company ID if present and valid, null otherwise
     */
    public Integer getCompanyIdFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("igniscore")
                    .build()
                    .verify(token)
                    .getClaim("companyId")
                    .asInt();

        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    /**
     * Generates the token expiration timestamp.
     *
     * <p>Current implementation:
     * Token expires 2 hours after issuance.
     *
     * @return expiration timestamp
     */
    private Instant getExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
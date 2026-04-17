package com.igniscore.api.dto;

/**
 * Data Transfer Object (DTO) representing the response returned after
 * a successful authentication request.
 *
 * <p>This record encapsulates the JWT token issued to the client,
 * which must be used in subsequent requests for authorization.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Provide the authentication token to the client</li>
 *     <li>Serve as a contract between backend and frontend after login</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Immutable by design (Java record)</li>
 *     <li>Contains only the token to keep response lightweight</li>
 *     <li>Can be extended in the future (e.g., expiration, refresh token)</li>
 * </ul>
 */
public record LoginResponseDTO(String token) {
}
package com.igniscore.api.dto;

/**
 * Data Transfer Object (DTO) used for authentication requests.
 *
 * <p>This record encapsulates user credentials required to perform login.
 * It is typically used by the authentication endpoint to receive input
 * from clients.
 *
 * <p>Fields:
 * <ul>
 *     <li>email    - user's email (used as username)</li>
 *     <li>password - user's raw password</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Immutable by design (Java record)</li>
 *     <li>Contains sensitive data and must not be logged or exposed</li>
 *     <li>Should be validated before authentication (e.g., non-null, format)</li>
 * </ul>
 */
public record AutDTO(
        String email,
        String password
) {
}
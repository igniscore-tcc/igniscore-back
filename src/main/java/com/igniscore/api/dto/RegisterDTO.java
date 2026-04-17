package com.igniscore.api.dto;

import com.igniscore.api.model.UserRole;

/**
 * Data Transfer Object (DTO) used for user registration requests.
 *
 * <p>This record encapsulates the data required to create a new user account.
 * It is typically used at the API boundary (e.g., controller layer) to receive
 * input from clients.
 *
 * <p>Fields:
 * <ul>
 *     <li>name     - user's full name</li>
 *     <li>email    - user's email (used as unique identifier)</li>
 *     <li>password - user's raw password (to be hashed before persistence)</li>
 *     <li>role     - user role defining access level</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Immutable by design (Java record)</li>
 *     <li>Should be validated before being processed (e.g., email format, password strength)</li>
 *     <li>Must not be exposed in responses due to sensitive data (password)</li>
 * </ul>
 */
public record RegisterDTO(
        String name,
        String email,
        String password,
        UserRole role
) {
}
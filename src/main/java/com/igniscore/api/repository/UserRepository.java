package com.igniscore.api.repository;

import com.igniscore.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Repository interface for {@link User} entity persistence.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD operations
 * and database interaction capabilities.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Handle persistence of User entities</li>
 *     <li>Provide query methods for user lookup</li>
 * </ul>
 *
 * <p>Notes:
 * <ul>
 *     <li>Email is used as a unique identifier for authentication</li>
 *     <li>The returned type {@link User} is expected to implement {@link UserDetails}</li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Retrieves a user by email.
     *
     * <p>This method is primarily used during authentication
     * to load user details based on the provided username (email).
     *
     * @param email user email (unique identifier)
     * @return user entity if found, otherwise null
     */
    User findByEmail(String email);
}
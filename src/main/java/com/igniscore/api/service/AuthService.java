package com.igniscore.api.service;

import com.igniscore.api.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service responsible for loading user-specific data during authentication.
 *
 * <p>This class integrates with Spring Security by implementing
 * {@link UserDetailsService}, allowing the framework to retrieve user
 * information based on a unique identifier (email in this case).
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Fetch user data from persistence layer</li>
 *     <li>Provide user details for authentication flow</li>
 * </ul>
 *
 * <p>Important:
 * The returned {@link UserDetails} must be compatible with Spring Security
 * expectations (e.g., contain credentials, roles, and account status).
 */
@Service
public class AuthService implements UserDetailsService {

    private final UserRepository repository;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository user persistence repository
     */
    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Loads a user by email for authentication purposes.
     *
     * <p>This method is invoked by Spring Security during login attempts.
     *
     * @param email user identifier (username)
     * @return user details required for authentication
     *
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserDetails user = repository.findByEmail(email);

        // Spring Security expects an exception, not null
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return user;
    }
}
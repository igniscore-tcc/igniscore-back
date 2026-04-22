package com.igniscore.api.service;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for retrieving the currently authenticated user
 * and associated tenant context from Spring Security.
 *
 * <p>Provides a centralized and consistent way to access authentication data,
 * enforcing fail-fast behavior when no valid user is present in the security context.
 *
 * <p><strong>Responsibilities:</strong>
 * <ul>
 *     <li>Extract the authenticated {@link User} from the security context</li>
 *     <li>Expose the current user's associated {@link Company}</li>
 *     <li>Enforce access control by throwing exceptions when authentication is invalid</li>
 * </ul>
 *
 * <p><strong>Security assumptions:</strong>
 * <ul>
 *     <li>The {@link org.springframework.security.core.Authentication#getPrincipal()}
 *         is expected to be an instance of {@link User}</li>
 *     <li>The security context must be properly populated by the authentication layer</li>
 * </ul>
 *
 * <p><strong>Failure behavior:</strong>
 * <ul>
 *     <li>Throws {@link AccessDeniedException} if no authenticated user is found</li>
 * </ul>
 */
@Service
public class AuthenticatedUserService {

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * <p>This method enforces that the principal is a valid {@link User}.
     *
     * @return the authenticated {@link User}
     * @throws AccessDeniedException if authentication is missing or invalid
     */
    public User getUserOrThrow() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User not authenticated");
        }

        return user;
    }

    /**
     * Retrieves the company associated with the currently authenticated user.
     *
     * <p>This method delegates to {@link #getUserOrThrow()} and assumes that
     * every user is associated with a {@link Company}.
     *
     * @return the {@link Company} of the authenticated user
     * @throws AccessDeniedException if the user is not authenticated
     */
    public Company getCompanyOrThrow() {
        return getUserOrThrow().getCompany();
    }
}
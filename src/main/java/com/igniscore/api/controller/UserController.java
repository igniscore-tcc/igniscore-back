package com.igniscore.api.controller;

import com.igniscore.api.dto.auth.RegisterDTO;
import com.igniscore.api.dto.user.ChangePasswordDTO;
import com.igniscore.api.dto.user.MeDTO;
import com.igniscore.api.dto.user.UserRegisterDTO;
import com.igniscore.api.model.User;
import com.igniscore.api.model.UserRole;
import com.igniscore.api.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Objects;


/**
 * GraphQL controller responsible for handling user-related queries and mutations.
 *
 * <p>This class acts as the entry point for GraphQL operations involving users,
 * delegating business logic to {@link UserService}.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Expose user queries (fetch users, fetch user by ID)</li>
 *     <li>Handle mutations related to user updates</li>
 *     <li>Bridge GraphQL requests to the service layer</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Uses Spring GraphQL annotations (@QueryMapping, @MutationMapping)</li>
 *     <li>Returns entity objects directly (can be replaced with DTOs)</li>
 *     <li>Thin controller — no business logic inside</li>
 * </ul>
 */
@Controller
public class UserController {

    /**
     * Service layer dependency for user operations.
     */
    private final UserService service;

    /**
     * Constructor-based dependency injection.
     *
     * @param service user service instance
     */
    public UserController(UserService service) {
        this.service = service;
    }

    /**
     * Updates the company associated with a user.
     *
     * @param cnpj company identifier
     * @return updated user
     */
    @MutationMapping
    public User updateUserCompany(@Argument String cnpj) {
        return service.updateUserCompany(cnpj);
    }

    /**
     * Updates user basic information.
     *
     * @param email user's email (identifier)
     * @param name  new name
     * @return updated user
     */
    @MutationMapping
    public User updateUser(@Argument String email, @Argument String name){
        return service.update(email, name);
    }

    @QueryMapping
    public MeDTO me(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        UserRole role = UserRole.valueOf(
                Objects.requireNonNull(authentication.getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority())
                        .replace("ROLE_", "")
        );

        Integer companyId = authentication.getDetails() != null
                ? (Integer) authentication.getDetails()
                : null;

        assert user != null;
        return new MeDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                role,
                user.isOnboarding(),
                companyId
        );
    }

    @MutationMapping
    public MeDTO completeOnboarding(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        assert user != null;
        User updatedUser = service.completeOnboarding(user.getId());

        UserRole role = UserRole.valueOf(
                Objects.requireNonNull(authentication.getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority())
                        .replace("ROLE_", "")
        );

        Integer companyId = authentication.getDetails() != null
                ? (Integer) authentication.getDetails()
                : null;

        return new MeDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                role,
                updatedUser.isOnboarding(),
                companyId
        );
    }

    /**
     * Retrieves users belonging to the authenticated user's company.
     *
     * @param page page number, starting from 0
     * @param size number of users per page
     * @return paginated users
     */
    @QueryMapping
    public Page<User> usersByCompany(
            @Argument Integer page,
            @Argument Integer size
    ) {
        PageRequest pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10
        );

        return service.findUsersByCompany(pageable);
    }

    @MutationMapping
    public String createEmployee(@Argument RegisterDTO data) {
        return service.store(data);
    }

    @MutationMapping
        public String userRegister(@Argument UserRegisterDTO data) {
        return service.userRegister(data);
    }

    @MutationMapping
    public String changeTemporaryPassword(@Argument ChangePasswordDTO data) {
        return service.changeTemporaryPassword(data);
    }
}
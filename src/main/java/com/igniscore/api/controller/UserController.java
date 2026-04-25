package com.igniscore.api.controller;

import com.igniscore.api.model.User;
import com.igniscore.api.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


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
     * Retrieves a single user by ID.
     *
     * @param id user identifier
     * @return user entity
     */
    @QueryMapping
    public User user(@Argument Integer id) {
        return service.findUserId(id);
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
}
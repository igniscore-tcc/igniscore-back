package com.igniscore.api.controller;

import com.igniscore.api.dto.ClientRegisterDTO;
import com.igniscore.api.dto.ClientUpdateDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * GraphQL controller responsible for handling queries and mutations
 * related to {@link Client} entities.
 *
 * <p>This layer acts as a thin transport adapter between the GraphQL API
 * and the service layer. It delegates all business logic to {@link ClientService}
 * and does not contain domain logic.
 *
 * <p><strong>Responsibilities:</strong>
 * <ul>
 *     <li>Expose GraphQL query and mutation endpoints</li>
 *     <li>Map GraphQL arguments to DTOs</li>
 *     <li>Delegate execution to the service layer</li>
 * </ul>
 *
 * <p><strong>Design notes:</strong>
 * <ul>
 *     <li>Follows a thin-controller pattern</li>
 *     <li>Relies on service layer for validation, authorization, and persistence</li>
 *     <li>Supports pagination via {@link Pageable} abstraction</li>
 * </ul>
 */
@Controller
@SuppressWarnings("unused")
public class ClientController {

    /**
     * Service responsible for client-related business operations.
     */
    private final ClientService service;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param service service layer handling client operations
     */
    @SuppressWarnings("unused")
    public ClientController(ClientService service) {
        this.service = service;
    }

    /**
     * GraphQL mutation that creates a new {@link Client}.
     *
     * <p>The input payload is mapped to {@link ClientRegisterDTO} and
     * forwarded to the service layer for validation and persistence.
     *
     * @param input client creation payload
     * @return the persisted {@link Client}
     */
    @MutationMapping
    @SuppressWarnings("unused")
    public Client storeClient(@Argument ClientRegisterDTO input) {
        return service.store(input);
    }

    /**
     * GraphQL mutation that updates an existing {@link Client}.
     *
     * <p>Supports partial updates. Only fields provided in the input
     * are applied to the target entity.
     *
     * @param input client update payload including identifier
     * @return the updated {@link Client}
     */
    @MutationMapping
    @SuppressWarnings("unused")
    public Client updateClient(@Argument ClientUpdateDTO input) {
        return service.update(input);
    }

    /**
     * GraphQL query that retrieves all clients within the current tenant scope.
     *
     * <p>Results are paginated using Spring Data {@link Pageable}.
     *
     * @param pageable pagination and sorting configuration
     * @return paginated {@link Client} result set
     */
    @QueryMapping
    @SuppressWarnings("unused")
    public Page<Client> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    /**
     * GraphQL query that retrieves a {@link Client} by its identifier.
     *
     * <p>The result is scoped to the authenticated user's company.
     *
     * @param id client identifier
     * @return matching {@link Client}
     */
    @QueryMapping
    @SuppressWarnings("unused")
    public Client client(@Argument Integer id){
        return service.findById(id);
    }

    /**
     * GraphQL mutation that deletes a {@link Client} by its identifier.
     *
     * <p>The operation is restricted to the current tenant scope.
     *
     * @param id client identifier
     * @return operation result message
     */
    @MutationMapping
    @SuppressWarnings("unused")
    public String deleteClient(@Argument Integer id){
        return service.delete(id);
    }
}
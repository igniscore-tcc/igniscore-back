package com.igniscore.api.controller;

import com.igniscore.api.dto.ClientQueryDTO;
import com.igniscore.api.dto.ClientRegisterDTO;
import com.igniscore.api.dto.ClientUpdateDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Client storeClient(@Argument @Valid ClientRegisterDTO input) {
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
    public Client updateClient(@Argument @Valid ClientUpdateDTO input) {
        return service.update(input);
    }

    /**
     * Retrieves a paginated list of {@link Client} entities belonging to the
     * authenticated tenant context.
     *
     * <p>This query applies deterministic ordering using the client identifier
     * in ascending order to guarantee stable pagination behavior across requests.
     * The response includes both the current page content and pagination metadata
     * wrapped inside {@link ClientQueryDTO}.
     *
     * <p><strong>Returned metadata:</strong>
     * <ul>
     *     <li>Total number of pages</li>
     *     <li>Total number of registered clients</li>
     *     <li>Current page content</li>
     * </ul>
     *
     * <p><strong>Pagination defaults:</strong>
     * <ul>
     *     <li>{@code page = 0}</li>
     *     <li>{@code size = 10}</li>
     * </ul>
     *
     * @param page zero-based page index
     * @param size maximum number of records per page
     * @return paginated client response with metadata
     */
    @QueryMapping
    @SuppressWarnings("unused")
    public ClientQueryDTO clients(@Argument Integer page, @Argument Integer size) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(Sort.Direction.ASC, "id")
        );

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
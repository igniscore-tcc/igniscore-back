package com.igniscore.api.controller;

import com.igniscore.api.dto.client.ClientQueryDTO;
import com.igniscore.api.dto.client.ClientRegisterDTO;
import com.igniscore.api.dto.client.ClientResponseDTO;
import com.igniscore.api.dto.client.ClientUpdateDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * GraphQL controller responsible for handling queries and mutations
 * related to client entities.
 *
 * <p>Delegates business operations to {@link ClientService}.
 */
@Controller
@RequiredArgsConstructor
public class ClientController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private final ClientService service;

    /**
     * Creates a new client.
     *
     * @param input client registration data
     * @return created client
     */
    @MutationMapping
    public Client storeClient(@Argument ClientRegisterDTO input) {
        return service.store(input);
    }

    /**
     * Updates an existing client.
     *
     * @param input client update data
     * @return updated client
     */
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @MutationMapping
    public Client updateClient(@Argument ClientUpdateDTO input) {
        return service.update(input);
    }

    /**
     * Retrieves a paginated list of clients.
     *
     * @param page zero-based page index
     * @param size maximum number of records per page
     * @return paginated client response
     */
    @QueryMapping
    public ClientQueryDTO clients(
            @Argument Integer page,
            @Argument Integer size
    ) {
        Pageable pageable = PageRequest.of(
                page != null ? page : DEFAULT_PAGE,
                size != null ? size : DEFAULT_SIZE,
                Sort.by(Sort.Direction.ASC, "id")
        );

        return service.findAll(pageable);
    }

    /**
     * Retrieves a client by identifier.
     *
     * @param id client identifier
     * @return client response
     */
    @QueryMapping
    public ClientResponseDTO client(@Argument Integer id) {
        return service.findById(id);
    }

    /**
     * Soft-deletes a client.
     *
     * @param id client identifier
     * @return operation result message
     */
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @MutationMapping
    public String deleteClient(@Argument Integer id) {
        return service.delete(id);
    }
}
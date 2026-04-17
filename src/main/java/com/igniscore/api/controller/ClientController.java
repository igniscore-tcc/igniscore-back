package com.igniscore.api.controller;

import com.igniscore.api.model.Client;
import com.igniscore.api.service.ClientService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller responsible for handling GraphQL queries and mutations
 * related to Client entities.
 * This class provides endpoints for creating, updating, retrieving,
 * and deleting clients. All business logic is delegated to the
 * ClientService layer.
 */
@Controller
public class ClientController {

    /**
     * Service responsible for client-related business operations.
     */
    private final ClientService service;

    /**
     * Constructor for ClientController.
     *
     * @param service the ClientService instance used to handle business logic
     */
    public ClientController(ClientService service) {
        this.service = service;
    }

    /**
     * GraphQL mutation used to create a new client.
     * This method receives client data as arguments from the GraphQL request
     * and forwards them to the service layer for processing and persistence.
     *
     * @param name   the client's name
     * @param cnpj   the client's CNPJ (for companies)
     * @param email  the client's email address
     * @param phone  the client's phone number
     * @param ie     the state registration number
     * @param ufIe   the state (UF) of the registration
     * @param obs    additional observations or notes about the client
     * @param cpf    the client's CPF (for individuals)
     * @return the created Client entity
     */
    @MutationMapping
    public Client storeClient(@Argument String name,
                              @Argument String cnpj,
                              @Argument String email,
                              @Argument String phone,
                              @Argument String ie,
                              @Argument String ufIe,
                              @Argument String obs,
                              @Argument String cpf) {
        return service.store(name, cnpj, email, phone, ie, ufIe, obs, cpf);
    }

    /**
     * GraphQL mutation used to update an existing client.
     * This method receives updated client data along with the client ID
     * and forwards them to the service layer for processing.
     *
     * @param id     the unique identifier of the client to update
     * @param name   the client's name
     * @param cnpj   the client's CNPJ (for companies)
     * @param email  the client's email address
     * @param phone  the client's phone number
     * @param ie     the state registration number
     * @param ufIe   the state (UF) of the registration
     * @param obs    additional observations or notes about the client
     * @param cpf    the client's CPF (for individuals)
     * @return the updated Client entity
     */
    @MutationMapping
    public Client updateClient(@Argument Integer id,
                               @Argument String name,
                               @Argument String cnpj,
                               @Argument String email,
                               @Argument String phone,
                               @Argument String ie,
                               @Argument String ufIe,
                               @Argument String obs,
                               @Argument String cpf) {
        return service.update(name, cnpj, email, phone, ie, ufIe, obs, cpf, id);
    }

    /**
     * GraphQL query used to retrieve all clients.
     *
     * @return a list of Client entities
     */
    @QueryMapping
    public List<Client> clients() {
        return service.findAll();
    }

    /**
     * GraphQL query used to retrieve a specific client by its ID.
     *
     * @param id the unique identifier of the client
     * @return the Client entity corresponding to the given ID
     */
    @QueryMapping
    public Client client(@Argument Integer id){
        return service.findClient(id);
    }

    /**
     * GraphQL mutation used to delete a client by its ID.
     *
     * @param id the unique identifier of the client to delete
     * @return a confirmation message indicating the result of the operation
     */
    @MutationMapping
    public String deleteClient(@Argument Integer id){
        return service.delete(id);
    }
}
package com.igniscore.api.controller;

import com.igniscore.api.model.Client;
import com.igniscore.api.service.ClientService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ClientController {

    private final ClientService service;


    public ClientController(ClientService service) {
        this.service = service;
    }

    @MutationMapping
    public Client createClient(@Argument String name,
                               @Argument String cnpj,
                               @Argument String email,
                               @Argument String phone,
                               @Argument String ie,
                               @Argument String ufIe,
                               @Argument String obs,
                               @Argument Integer cpf) {
        return service.createClient(name, cnpj, email, phone, ie, ufIe, obs, cpf);
    }


    @MutationMapping
    public Client updateClient(@Argument Integer id,
                               @Argument String name,
                               @Argument String cnpj,
                               @Argument String email,
                               @Argument String phone,
                               @Argument String ie,
                               @Argument String ufIe,
                               @Argument String obs,
                               @Argument Integer cpf) {
        return service.updateClient(name, cnpj, email, phone, ie, ufIe, obs, id, cpf);
    }

    @QueryMapping
    public List<Client> clients() {
        return service.findAll();
    }

    @QueryMapping
    public Client client(@Argument Integer id){
        return service.findClient(id);
    }

    @MutationMapping
    public String deleteClient(@Argument Integer id){
        return service.deleteClient(id);
    }
}

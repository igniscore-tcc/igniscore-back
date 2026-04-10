package com.igniscore.api.controller;

import com.igniscore.api.model.Client;
import com.igniscore.api.service.ClientService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

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
                               @Argument Integer number,
                               @Argument String ie,
                               @Argument String uf_ie,
                               @Argument String obs) {
        return service.createClient(name, cnpj, email, number, ie, uf_ie, obs);
    }


    @MutationMapping
    public Client updateClient(@Argument Integer id,
                               @Argument String name,
                               @Argument String cnpj,
                               @Argument String email,
                               @Argument Integer number,
                               @Argument String ie,
                               @Argument String uf_ie,
                               @Argument String obs) {
        return service.updateClient(name, cnpj, email, number, ie, uf_ie, obs, id);
    }
}

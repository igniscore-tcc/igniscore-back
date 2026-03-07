package com.igniscore.api.controller;

import com.igniscore.api.model.Company;
import com.igniscore.api.service.CompanyService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CompanyController {

    private final CompanyService service;

    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @QueryMapping
    public List<Company> companies() {
        return service.findAll();
    }

    @MutationMapping
    public Company createCompany(@Argument String name,
            @Argument String cnpj,
            @Argument String ie,
            @Argument String ufIe,
            @Argument String email,
            @Argument String phone) {
        return service.create(name, cnpj, ie, ufIe, email, phone);
    }
}

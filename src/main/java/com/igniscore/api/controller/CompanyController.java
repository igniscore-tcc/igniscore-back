package com.igniscore.api.controller;

import com.igniscore.api.model.Company;
import com.igniscore.api.service.CompanyService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller responsible for handling GraphQL queries and mutations
 * related to Company entities.
 * This class exposes endpoints for retrieving and creating companies,
 * delegating all business logic to the CompanyService layer.
 */
@Controller
public class CompanyController {

    /**
     * Service responsible for company-related business operations.
     */
    private final CompanyService service;

    /**
     * Constructor for CompanyController.
     *
     * @param service the CompanyService instance used to handle business logic
     */
    public CompanyController(CompanyService service) {
        this.service = service;
    }

    /**
     * GraphQL query used to retrieve all companies.
     * This method calls the service layer to fetch a list of all
     * registered companies.
     *
     * @return a list of Company entities
     */
    @QueryMapping
    public List<Company> companies() {
        return service.findAll();
    }

    /**
     * GraphQL mutation used to create a new company.
     * This method receives company data as arguments from the GraphQL request
     * and forwards them to the service layer for processing and persistence.
     *
     * @param name   the name of the company
     * @param cnpj   the CNPJ (Brazilian company registration number)
     * @param ie     the state registration number
     * @param ufIe   the state (UF) of the registration
     * @param email  the company's contact email
     * @param phone  the company's contact phone number
     * @return the created Company entity
     */
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
package com.igniscore.api.service;

import com.igniscore.api.model.Company;
import com.igniscore.api.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing {@link Company} entities.
 *
 * <p>This class provides operations for:
 * <ul>
 *     <li>Retrieving companies</li>
 *     <li>Creating new companies</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Acts as an abstraction layer over {@link CompanyRepository}</li>
 *     <li>Encapsulates basic business logic for company management</li>
 * </ul>
 */
@Service
public class CompanyService {

    private final CompanyRepository repository;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository company persistence repository
     */
    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all companies from the database.
     *
     * @return list of all companies
     */
    public List<Company> findAll() {
        return repository.findAll();
    }

    /**
     * Creates and persists a new company.
     *
     * <p>This method constructs a {@link Company} entity from input parameters
     * and saves it to the database.
     *
     * @param name   company name
     * @param cnpj   company CNPJ (Brazilian business identifier)
     * @param ie     state registration (Inscrição Estadual)
     * @param ufIe   state abbreviation for IE
     * @param email  company contact email
     * @param phone  company contact phone
     *
     * @return persisted company entity
     */
    public Company create(String name, String cnpj, String ie,
                          String ufIe, String email, String phone) {

        Company company = new Company();

        company.setName(name);
        company.setCnpj(cnpj);
        company.setIe(ie);
        company.setUfIe(ufIe);
        company.setEmail(email);
        company.setPhone(phone);

        return repository.save(company);
    }
}
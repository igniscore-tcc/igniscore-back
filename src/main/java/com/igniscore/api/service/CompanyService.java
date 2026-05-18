package com.igniscore.api.service;

import com.igniscore.api.dto.CreateCompanyDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.CompanyRepository;
import com.igniscore.api.repository.UserRepository;
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
    private final AuthenticatedUserService authUserService;
    private final UserRepository userRepository;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository company persistence repository
     */
    public CompanyService(CompanyRepository repository, AuthenticatedUserService authUserService, UserRepository userRepository) {
        this.repository = repository;
        this.authUserService = authUserService;
        this.userRepository = userRepository;
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
     *
     * @return persisted company entity
     */
    public Company storeCompany(CreateCompanyDTO dto) {

        User user = this.authUserService.getUserOrThrow();

        Company company = new Company();

        company.setName(dto.getName());
        company.setCnpj(dto.getCnpj());
        company.setIe(dto.getIe());
        company.setUfIe(dto.getUfIe());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());

        Company savedCompany = repository.save(company);

        user.setCompany(savedCompany);
        userRepository.save(user);

        return savedCompany;
    }
}
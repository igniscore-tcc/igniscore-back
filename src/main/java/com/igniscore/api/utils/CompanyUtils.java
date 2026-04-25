package com.igniscore.api.utils;

import com.igniscore.api.model.Company;
import com.igniscore.api.repository.CompanyRepository;
import org.springframework.stereotype.Service;

/**
 * Utility service for resolving and validating {@link Company} entities.
 *
 * <p>This class centralizes common company-related lookup operations,
 * ensuring consistent behavior when retrieving companies from persistence.
 *
 * <p>Design note:
 * In larger systems, this logic is often part of a dedicated domain/service layer
 * rather than a "utils" class, to better reflect business responsibility.
 */
@Service
public class CompanyUtils {

    private final CompanyRepository repository;

    /**
     * Constructor-based dependency injection (preferred over field injection).
     *
     * @param repository company persistence repository
     */
    public CompanyUtils(CompanyRepository repository) {
        this.repository = repository;
    }

    /**
     * Validates that a company exists by its identifier.
     *
     * @param cnpj company identifier
     * @return existing company
     *
     * @throws RuntimeException if the company is not found
     */
    public Company existsCompany(String cnpj) {
        return repository.findByCnpj(cnpj).orElseThrow(() -> new RuntimeException("Company not found"));
    }

    /**
     * Retrieves the company associated with the currently authenticated user.
     *
     * <p>This method is typically used in multi-tenant contexts where
     * data access must be restricted to the user's company.
     *
     * @param id company identifier (usually derived from authenticated user context)
     * @return company entity
     *
     * @throws RuntimeException if the company is not found
     */
    public Company loggedCompany(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }
}
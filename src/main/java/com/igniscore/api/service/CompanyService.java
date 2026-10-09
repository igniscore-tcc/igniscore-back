package com.igniscore.api.service;

import com.igniscore.api.dto.company.CreateCompanyDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.CompanyRepository;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.utils.AuditUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for managing company entities.
 */
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repository;
    private final AuthenticatedUserService authUserService;
    private final UserRepository userRepository;
    private final AuditUtils audit;

    /**
     * Retrieves all companies.
     *
     * @return list of companies
     */
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        return repository.findAll();
    }

    /**
     * Creates a company and associates it with the authenticated user.
     *
     * @param dto company registration data
     * @return persisted company
     */
    @Transactional
    public Company storeCompany(CreateCompanyDTO dto) {
        User user = authUserService.getUserOrThrow();

        if (dto.getCnpj() == null || dto.getCnpj().isBlank()) {
            throw new IllegalArgumentException("CNPJ is required");
        }

        Company company = new Company(dto);
        Company savedCompany = repository.save(company);

        user.setCompany(savedCompany);
        userRepository.save(user);

        audit.newAudit(
                user,
                savedCompany,
                "Company",
                "Create",
                null,
                savedCompany
        );

        return savedCompany;
    }

    /**
     * Retrieves the authenticated user's company.
     *
     * @return optional containing the company, if found
     */
    @Transactional(readOnly = true)
    public Optional<Company> myCompany() {
        Integer companyId = authUserService.getCompanyOrThrow().getId();

        return repository.findById(companyId);
    }
}
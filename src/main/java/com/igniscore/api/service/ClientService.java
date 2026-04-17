package com.igniscore.api.service;

import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.utils.CompanyUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsible for managing {@link Client} entities.
 *
 * <p>This class encapsulates business rules such as:
 * <ul>
 *     <li>Ensuring every client is associated with a company</li>
 *     <li>Enforcing multi-tenant data isolation</li>
 *     <li>Validating ownership based on the authenticated user</li>
 * </ul>
 *
 * <p>Assumptions:
 * <ul>
 *     <li>Every authenticated user belongs to a {@link Company}</li>
 *     <li>Clients cannot be accessed across different companies</li>
 * </ul>
 */
@Service
public class ClientService {

    private final ClientRepository repository;
    private final CompanyUtils companyUtils;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor with dependency injection.
     *
     * @param repository   client persistence repository
     * @param companyUtils utility for resolving the logged user's company
     */
    public ClientService(ClientRepository repository, CompanyUtils companyUtils) {
        this.repository = repository;
        this.companyUtils = companyUtils;
    }

    /**
     * Creates and persists a new client linked to the authenticated user's company.
     *
     * <p>Flow:
     * <ol>
     *     <li>Retrieve authenticated user</li>
     *     <li>Resolve user's company</li>
     *     <li>Create Client entity</li>
     *     <li>Persist and refresh state from database</li>
     * </ol>
     *
     * @return persisted client with up-to-date database state
     * @throws RuntimeException if no authenticated user is found
     */
    @Transactional
    public Client store(String name, String cnpj, String email, String phone,
                        String ie, String ufIe, String obs, String cpf) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure a valid authenticated user exists
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found.");
        }

        // Resolve company in a multi-tenant safe way
        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        // Create client entity
        Client client = new Client();
        client.setName(name);
        client.setCnpj(cnpj);
        client.setEmail(email);
        client.setPhone(phone);
        client.setIe(ie);
        client.setUfIe(ufIe);
        client.setObs(obs);
        client.setCpf(cpf);
        client.setCompany(company);

        // Persist entity
        Client saved = repository.save(client);

        // Sync with database state (e.g., triggers, defaults)
        entityManager.refresh(saved);

        return saved;
    }

    /**
     * Updates an existing client using partial update semantics.
     *
     * <p>Only non-null fields are updated.
     *
     * @param id client identifier
     * @return updated client
     *
     * @throws RuntimeException if client does not exist or access is denied
     */
    @Transactional
    public Client update(String name, String cnpj, String email, String phone,
                         String ie, String ufIe, String obs, String cpf, Integer id) {

        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found."));

        Company company = getCompany();

        // Security check (multi-tenant ownership)
        if (!client.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Access denied.");
        }

        // Partial update (null-safe)
        if (name != null) client.setName(name);
        if (cnpj != null) client.setCnpj(cnpj);
        if (email != null) client.setEmail(email);
        if (phone != null) client.setPhone(phone);
        if (ie != null) client.setIe(ie);
        if (ufIe != null) client.setUfIe(ufIe);
        if (obs != null) client.setObs(obs);
        if (cpf != null) client.setCpf(cpf);

        return repository.save(client);
    }

    /**
     * Retrieves all clients belonging to the authenticated user's company.
     *
     * @return list of clients for the company
     */
    public List<Client> findAll() {
        return repository.findByCompany(getCompany());
    }

    /**
     * Retrieves a specific client ensuring company-level isolation.
     *
     * @param id client identifier
     * @return client entity
     *
     * @throws RuntimeException if client does not exist or does not belong to the company
     */
    public Client findClient(Integer id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found."));

        Company company = getCompany();

        if (!client.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("This client does not belong to this company.");
        }

        return client;
    }

    /**
     * Deletes a client ensuring company-level isolation.
     *
     * @param id client identifier
     * @return success message
     *
     * @throws RuntimeException if client does not exist or access is denied
     */
    @Transactional
    public String delete(Integer id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found."));

        Company company = getCompany();

        if (!client.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("This client does not belong to this company.");
        }

        repository.delete(client);

        return "Client successfully deleted.";
    }

    /**
     * Resolves the company of the currently authenticated user.
     *
     * <p>This method centralizes authentication context logic and
     * should be used wherever tenant isolation is required.
     *
     * @return company associated with the authenticated user
     * @throws RuntimeException if no authenticated user is found
     */
    public Company getCompany() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found.");
        }

        return companyUtils.loggedCompany(loggedUser.getCompany().getId());
    }
}
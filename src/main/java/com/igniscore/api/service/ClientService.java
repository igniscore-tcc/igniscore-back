package com.igniscore.api.service;

import com.igniscore.api.dto.ClientQueryDTO;
import com.igniscore.api.dto.ClientRegisterDTO;
import com.igniscore.api.dto.ClientUpdateDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.utils.AuditUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

/**
 * Service layer responsible for managing {@link Client} entities.
 *
 * <p>Encapsulates business logic, transactional boundaries, and
 * multi-tenant data isolation rules. All operations are scoped to the
 * {@link Company} associated with the authenticated user.
 *
 * <p><strong>Core responsibilities:</strong>
 * <ul>
 *     <li>Create, update, retrieve, and delete {@link Client} entities</li>
 *     <li>Enforce tenant isolation at the service layer</li>
 *     <li>Apply validation and partial update semantics</li>
 * </ul>
 *
 * <p><strong>Multi-tenancy model:</strong>@Transactional
    public String delete(Integer id) {
        Company = authUserService.getCompanyOrThrow();
        Client = getClientOrThrow(id, company);
        repository.delete(client);
        return "Client successfully deleted.";
    }
 * <ul>
 *     <li>Each {@link Client} is owned by a {@link Company}</li>
 *     <li>All queries are constrained by company context</li>
 *     <li>Cross-tenant access is prevented via repository filtering</li>
 * </ul>
 *
 * <p><strong>Transaction model:</strong>
 * <ul>
 *     <li>Write operations are executed within transactional boundaries</li>
 *     <li>Read operations are marked as {@code readOnly = true} for optimization</li>
 * </ul>
 *
 * <p><strong>Failure behavior:</strong>
 * <ul>
 *     <li>Throws {@link AccessDeniedException} when authentication is invalid</li>
 *     <li>Throws {@link EntityNotFoundException} when a client is not found
 *         within the current tenant scope</li>
 *     <li>Throws {@link IllegalArgumentException} for invalid input state</li>
 * </ul>
 */
@Service
public class ClientService {

    private final ClientRepository repository;
    private final AuthenticatedUserService authUserService;
    private final AuditUtils audit;

    @PersistenceContext
    @SuppressWarnings("unused")
    private EntityManager entityManager;

    /**
     * Constructs the service with required dependencies.
     *
     * @param repository persistence layer for {@link Client}
     * @param authUserService service for retrieving authenticated user context
     */
    public ClientService(
            ClientRepository repository,
            AuthenticatedUserService authUserService,
            AuditUtils audit
    ) {
        this.repository = repository;
        this.authUserService = authUserService;
        this.audit = audit;
    }

    /**
     * Creates and persists a new {@link Client} associated with the
     * authenticated user's company.
     *
     * <p><strong>Execution flow:</strong>
     * <ol>
     *     <li>Resolve authenticated user's company</li>
     *     <li>Validate DTO-level business constraints</li>
     *     <li>Map DTO to entity</li>
     *     <li>Persist entity and refresh from database</li>
     * </ol>
     *
     * @param dto input payload for client creation
     * @return persisted {@link Client} with database-synchronized state
     * @throws AccessDeniedException if no authenticated user is available
     * @throws IllegalArgumentException if CPF/CNPJ constraint is violated
     */
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public Client store(ClientRegisterDTO dto) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        if (!dto.isCpfOrCnpjValid()) {
            throw new IllegalArgumentException("CPF or CNPJ must be provided");
        }

        Client client = new Client();
        client.setName(dto.getName());
        client.setCnpj(dto.getCnpj());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setIe(dto.getIe());
        client.setUfIe(dto.getUfIe());
        client.setObs(dto.getObs());
        client.setCpf(dto.getCpf());
        client.setCompany(company);

        Client saved = repository.save(client);


        audit.newAudit(
                user,
                company,
                "Client",
                "Create",
                null,
                saved
        );


        // Ensures entity state reflects database-side changes (e.g., triggers, defaults)
        entityManager.refresh(saved);

        return saved;
    }

    /**
     * Updates an existing {@link Client} using partial update semantics.
     *
     * <p>Only non-null fields in the DTO are applied to the entity.
     * Fields omitted in the request remain unchanged.
     *
     * @param dto input payload containing update data and target identifier
     * @return updated {@link Client}
     * @throws AccessDeniedException if authentication is invalid
     * @throws EntityNotFoundException if the client does not exist within the tenant scope
     */
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public Client update(ClientUpdateDTO dto) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();
        Client client = getClientOrThrow(dto.getId(), company);

        Client oldData = new Client();

        oldData.setId(client.getId());
        oldData.setName(client.getName());
        oldData.setCnpj(client.getCnpj());
        oldData.setEmail(client.getEmail());
        oldData.setPhone(client.getPhone());
        oldData.setIe(client.getIe());
        oldData.setUfIe(client.getUfIe());
        oldData.setObs(client.getObs());
        oldData.setCpf(client.getCpf());

        if (dto.getName() != null) client.setName(dto.getName());
        if (dto.getCnpj() != null) client.setCnpj(dto.getCnpj());
        if (dto.getEmail() != null) client.setEmail(dto.getEmail());
        if (dto.getPhone() != null) client.setPhone(dto.getPhone());
        if (dto.getIe() != null) client.setIe(dto.getIe());
        if (dto.getUfIe() != null) client.setUfIe(dto.getUfIe());
        if (dto.getObs() != null) client.setObs(dto.getObs());
        if (dto.getCpf() != null) client.setCpf(dto.getCpf());

        Client updated = repository.save(client);

        audit.newAudit(
                user,
                company,
                "Client",
                "Update",
                oldData,
                updated
        );

        return updated;
    }

    /**
     * Retrieves a {@link Client} by identifier within the current tenant scope.
     *
     * @param id client identifier
     * @return matching {@link Client}
     * @throws AccessDeniedException if authentication is invalid
     * @throws EntityNotFoundException if not found within the company scope
     */
    @Transactional(readOnly = true)
    public Client findById(Integer id) {
        Company company = authUserService.getCompanyOrThrow();
        return getClientOrThrow(id, company);
    }

    /**
     * Deletes a {@link Client} within the current tenant scope.
     *
     * @param id client identifier
     * @return confirmation message
     * @throws AccessDeniedException if authentication is invalid
     * @throws EntityNotFoundException if not found within the company scope
     */
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public String delete(Integer id) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Client client = getClientOrThrow(id, company);

        audit.newAudit(
                user,
                company,
                "Client",
                "Delete",
                client,
                null
        );

        repository.delete(client);

        return "Client successfully deleted.";
    }

    /**
     * Resolves a client by identifier and company, enforcing tenant isolation.
     *
     * @param id client identifier
     * @param company tenant context
     * @return resolved {@link Client}
     * @throws EntityNotFoundException if no matching client is found
     */
    private Client getClientOrThrow(Integer id, Company company) {
        return repository.findByIdAndCompany(id, company)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    /**
     * Retrieves paginated {@link Client} records visible to the authenticated tenant.
     *
     * <p>This operation applies pagination constraints through Spring Data's
     * {@link Pageable} abstraction and returns both the current page content
     * and pagination metadata wrapped inside {@link ClientQueryDTO}.
     *
     * <p><strong>Returned metadata:</strong>
     * <ul>
     *     <li>Total number of pages</li>
     *     <li>Total number of registered clients</li>
     *     <li>Current page content</li>
     * </ul>
     *
     * <p><strong>Execution flow:</strong>
     * <ol>
     *     <li>Create pagination configuration using page number and size</li>
     *     <li>Execute paginated repository query</li>
     *     <li>Extract content and pagination metadata</li>
     *     <li>Build response DTO</li>
     * </ol>
     *
     * @return paginated client response containing records and metadata
     */
    @Cacheable(
            value = "clients",
            key = "@cacheKeyService.clientsKey(#pageable)",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public ClientQueryDTO findAll(Pageable pageable) {
        Company company = authUserService.getCompanyOrThrow();

        Page<Client> page = repository.findByCompany(company, pageable);

        return new ClientQueryDTO(
                page.getContent(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
package com.igniscore.api.service;

import com.igniscore.api.dto.client.ClientQueryDTO;
import com.igniscore.api.dto.client.ClientRegisterDTO;
import com.igniscore.api.dto.client.ClientResponseDTO;
import com.igniscore.api.dto.client.ClientUpdateDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.service.subscription.RequiresSubscriptionAccess;
import com.igniscore.api.utils.AuditUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * Service responsible for managing clients within the authenticated company.
 *
 * <p>Enforces tenant isolation, transactional boundaries, audit logging,
 * caching and soft deletion.
 */
@RequiresSubscriptionAccess
@Service
@RequiredArgsConstructor
public class ClientService {

    private static final String ENTITY_NAME = "Client";

    private final ClientRepository repository;
    private final AuthenticatedUserService authUserService;
    private final AuditUtils audit;
    private final EntityManager entityManager;

    /**
     * Creates a client associated with the authenticated company.
     *
     * @param dto client registration data
     * @return persisted client
     */
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public Client store(ClientRegisterDTO dto) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Client client = new Client(dto, company);
        Client saved = repository.save(client);

        entityManager.refresh(saved);

        auditClient(user, company, "Create", null, saved);

        return saved;
    }

    /**
     * Updates an existing client using partial update semantics.
     * Null fields are ignored.
     *
     * @param dto client update data
     * @return updated client
     */
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public Client update(ClientUpdateDTO dto) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Client client = getClientOrThrow(dto.getId(), company);
        Client oldData = new Client(client);

        client.update(dto);

        auditClient(user, company, "Update", oldData, client);

        return client;
    }

    /**
     * Retrieves a client by ID within the authenticated company.
     *
     * @param id client identifier
     * @return client response
     */
    @Transactional(readOnly = true)
    public ClientResponseDTO findById(Integer id) {
        Company company = authUserService.getCompanyOrThrow();

        return new ClientResponseDTO(getClientOrThrow(id, company));
    }

    /**
     * Soft-deletes a client within the authenticated company.
     *
     * @param id client identifier
     * @return deletion confirmation
     */
    @Transactional
    @CacheEvict(value = "clients", allEntries = true)
    public String delete(Integer id) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Client client = getClientOrThrow(id, company);

        Client oldData = new Client(client);
        client.setDeletedAt(new Timestamp(System.currentTimeMillis()));

        auditClient(user, company, "Delete", oldData, client);

        return "Client successfully deleted.";
    }

    /**
     * Retrieves a paginated list of active clients belonging to the
     * authenticated company.
     *
     * @param pageable pagination configuration
     * @return paginated client response
     */
    @Cacheable(
            value = "clients",
            key = "@cacheKeyService.clientsKey(#pageable)",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public ClientQueryDTO findAll(Pageable pageable) {
        Company company = authUserService.getCompanyOrThrow();

        Page<Client> page = repository.findByCompanyAndDeletedAtIsNull(
                company,
                pageable
        );

        List<ClientResponseDTO> clients = page.getContent()
                .stream()
                .map(ClientResponseDTO::new)
                .toList();

        return new ClientQueryDTO(
                clients,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    /**
     * Resolves an active client within the specified company.
     *
     * @param id client identifier
     * @param company tenant context
     * @return matching client
     * @throws EntityNotFoundException if the client does not exist
     *                                  within the company
     */
    private Client getClientOrThrow(Integer id, Company company) {
        return repository.findByIdAndCompanyAndDeletedAtIsNull(id, company)
                .orElseThrow(() ->
                        new EntityNotFoundException("Client not found")
                );
    }

    /**
     * Records a client operation in the audit log.
     *
     * @param user authenticated user
     * @param company associated company
     * @param action operation performed
     * @param oldData previous client state, if applicable
     * @param newData resulting client state, if applicable
     */
    private void auditClient(
            User user,
            Company company,
            String action,
            Client oldData,
            Client newData
    ) {
        audit.newAudit(
                user,
                company,
                ENTITY_NAME,
                action,
                oldData,
                newData
        );
    }
}
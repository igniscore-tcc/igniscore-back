package com.igniscore.api.repository;

import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing persistence operations of the {@link Client} entity.
 *
 * <p>Extends {@link JpaRepository}, providing out-of-the-box CRUD operations,
 * pagination, and sorting capabilities. The implementation is generated
 * automatically by Spring Data JPA at runtime.
 *
 * <p><strong>Key responsibilities:</strong>
 * <ul>
 *     <li>Provide data access operations for {@link Client}</li>
 *     <li>Support query derivation based on method naming conventions</li>
 *     <li>Enforce logical data isolation in multi-tenant scenarios via {@link Company}</li>
 * </ul>
 *
 * <p><strong>Domain assumptions:</strong>
 * <ul>
 *     <li>Each {@link Client} is associated with a single {@link Company}</li>
 *     <li>All access patterns are scoped by Company to prevent cross-tenant data access</li>
 * </ul>
 *
 * <p><strong>Notes:</strong>
 * <ul>
 *     <li>No manual implementation is required</li>
 *     <li>Additional queries can be defined using Spring Data method naming conventions
 *         or explicit query annotations</li>
 * </ul>
 */
public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * Returns a paginated list of clients associated with the given company.
     *
     * <p>This method is intended for multi-tenant data access, ensuring that
     * only clients belonging to the specified company are retrieved.
     *
     * @param company the {@link Company} used as a filter
     * @param pageable pagination and sorting information
     * @return a {@link Page} of clients scoped to the given company
     */
    Page<Client> findByCompany(Company company, Pageable pageable);

    /**
     * Retrieves a client by its identifier and associated company.
     *
     * <p>This method enforces tenant isolation by ensuring the client
     * belongs to the specified company.
     *
     * @param id the client identifier
     * @param company the {@link Company} used as a filter
     * @return an {@link Optional} containing the client if found within the given company scope
     */
    Optional<Client> findByIdAndCompany(Integer id, Company company);
}
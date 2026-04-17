package com.igniscore.api.repository;

import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for {@link Client} entity persistence.
 *
 * <p>Extends {@link JpaRepository}, providing standard CRUD operations
 * and database interaction capabilities without requiring explicit implementation.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Persist and retrieve Client entities</li>
 *     <li>Support multi-tenant queries based on Company ownership</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Clients are associated with a {@link Company}</li>
 *     <li>Query methods enforce logical data isolation at the repository level</li>
 *     <li>Additional queries can be defined using Spring Data conventions</li>
 * </ul>
 */
public interface ClientRepository extends JpaRepository<Client, Integer> {

    /**
     * Retrieves all clients belonging to a specific company.
     *
     * <p>This method is typically used to enforce multi-tenant data access,
     * ensuring that users only retrieve clients associated with their company.
     *
     * @param company company entity used as filter
     * @return list of clients associated with the given company
     */
    List<Client> findByCompany(Company company);
}
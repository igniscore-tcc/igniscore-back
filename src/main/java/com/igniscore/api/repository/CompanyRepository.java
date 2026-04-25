package com.igniscore.api.repository;

import com.igniscore.api.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for {@link Company} entity persistence.
 *
 * <p>Extends {@link JpaRepository}, providing a complete set of
 * CRUD (Create, Read, Update, Delete) operations out of the box.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Persist Company entities</li>
 *     <li>Retrieve companies by identifier or other query methods</li>
 *     <li>Delete and update company records</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>No custom queries are defined yet</li>
 *     <li>Additional query methods can be added using Spring Data naming conventions</li>
 *     <li>Serves as the primary data access layer for company-related operations</li>
 * </ul>
 */
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByCnpj(String cnpj);
}
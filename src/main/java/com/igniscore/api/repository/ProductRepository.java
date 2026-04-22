package com.igniscore.api.repository;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@link Product} entity persistence.
 *
 * <p>Extends {@link JpaRepository}, providing standard CRUD operations
 * and database interaction capabilities without requiring explicit implementation.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Persist and retrieve Product entities</li>
 *     <li>Provide built-in CRUD operations (save, findById, delete, etc.)</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Custom query methods can be added as needed using Spring Data conventions</li>
 *     <li>Acts as the data access layer for product-related operations</li>
 * </ul>
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByCompany(Company company);
}
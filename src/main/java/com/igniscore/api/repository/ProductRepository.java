package com.igniscore.api.repository;

import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository responsible for persistence and retrieval operations
 * related to {@link Product} entities.
 *
 * <p>This interface extends {@link JpaRepository}, inheriting standard
 * CRUD operations and pagination support provided by Spring Data JPA.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Persisting product entities</li>
 *     <li>Retrieving products scoped by company ownership</li>
 *     <li>Supporting paginated product queries</li>
 *     <li>Optimizing association loading through entity graphs</li>
 * </ul>
 *
 * <p>Performance notes:
 * <ul>
 *     <li>{@link EntityGraph} is used to eagerly load the associated
 *     {@link Company} entity when required</li>
 *     <li>This approach helps reduce N+1 query issues during
 *     product retrieval operations</li>
 * </ul>
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Retrieves a paginated list of products belonging to a company
     * filtered by logical status.
     *
     * <p>Results are ordered by product identifier in ascending order.
     *
     * <p>The associated {@link Company} entity is eagerly loaded
     * through an entity graph.
     *
     * @param company company owner of the products
     * @param status logical product status filter
     * @param pageable pagination and sorting configuration
     * @return paginated list of matching products
     */
    @EntityGraph(attributePaths = {"company"})
    Page<Product> findByCompanyAndStatusOrderByIdAsc(
            Company company,
            Boolean status,
            Pageable pageable
    );

    /**
     * Retrieves a product by its identifier.
     *
     * <p>The associated {@link Company} entity is eagerly loaded
     * through an entity graph.
     *
     * @param id product identifier
     * @return optional containing the product when found
     */
    @EntityGraph(attributePaths = {"company"})
    Optional<Product> findByIdActive(Integer id);
}
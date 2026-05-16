package com.igniscore.api.service;

import com.igniscore.api.dto.ProductStoreDTO;
import com.igniscore.api.dto.ProductUpdateDTO;
import com.igniscore.api.model.User;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.utils.AuditUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer responsible for lifecycle management of {@link Product} entities.
 *
 * <p>This service encapsulates all business operations related to products,
 * including creation, update, retrieval, and soft deletion, while enforcing
 * strict multi-tenant isolation through the authenticated user's company context.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Persisting products associated with a company</li>
 *     <li>Applying partial updates to existing products</li>
 *     <li>Performing logical deletion using the status flag</li>
 *     <li>Restricting access to company-owned resources only</li>
 *     <li>Providing paginated retrieval of active products</li>
 *     <li>Generating audit records for mutating operations</li>
 *     <li>Managing cache invalidation and cached reads</li>
 * </ul>
 *
 * <p>All operations assume a valid authenticated context provided by
 * {@link AuthenticatedUserService}.
 */
@Service
public class ProductService {

    private final ProductRepository repository;
    private final AuthenticatedUserService authUserService;
    private final AuditUtils audit;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository repository responsible for product persistence
     * @param authUserService service responsible for authenticated user context resolution
     */
    public ProductService(ProductRepository repository,
                          AuthenticatedUserService authUserService,
                          AuditUtils audit
    ) {
        this.repository = repository;
        this.authUserService = authUserService;
        this.audit = audit;
    }

    @PersistenceContext
    @SuppressWarnings("unused")
    private EntityManager entityManager;

    /**
     * Creates and persists a new {@link Product} associated with the authenticated company.
     *
     * <p>The created product is initialized as active ({@code status = true})
     * and linked to the authenticated user's company.
     *
     * <p>After persistence, the entity state is refreshed from the database
     * to guarantee synchronization with generated values and persistence-side updates.
     *
     * <p>An audit record is generated describing the creation event.
     *
     * <p>Flow:
     * <ol>
     *     <li>Resolve authenticated user and company</li>
     *     <li>Map DTO fields into a new entity instance</li>
     *     <li>Persist entity</li>
     *     <li>Create audit entry</li>
     *     <li>Refresh entity state</li>
     * </ol>
     *
     * @param dto DTO containing product creation data
     * @return persisted {@link Product} entity
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product store(ProductStoreDTO dto) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Product product = new Product();

        product.setName(dto.getName());
        product.setType(dto.getType());
        product.setValidity(dto.getValidity());
        product.setLot(dto.getLot());
        product.setPrice(dto.getPrice());
        product.setCompany(company);
        product.setStatus(true);

        Product saved = repository.save(product);

        audit.newAudit(
                user,
                company,
                "Product",
                "Create",
                null,
                saved
        );

        entityManager.refresh(saved);

        return saved;
    }

    /**
     * Updates an existing {@link Product} using partial update semantics.
     *
     * <p>Only non-null values provided by the DTO are applied to the entity.
     * Existing values remain unchanged when the corresponding DTO field is {@code null}.
     *
     * <p>The target product must belong to the authenticated user's company.
     *
     * <p>A snapshot containing the previous entity state is created before mutation
     * to support audit logging.
     *
     * <p>An audit record is generated describing the update operation.
     *
     * <p>Flow:
     * <ol>
     *     <li>Resolve authenticated user and company</li>
     *     <li>Validate product ownership</li>
     *     <li>Capture previous state for auditing</li>
     *     <li>Apply partial updates</li>
     *     <li>Persist updated entity</li>
     *     <li>Create audit entry</li>
     * </ol>
     *
     * @param dto DTO containing update data and product identifier
     * @return updated {@link Product} entity
     *
     * @throws RuntimeException if the product does not exist
     *         or does not belong to the authenticated company
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product update(ProductUpdateDTO dto) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Product product = getProductForCompany(dto.getId(), company);

        Product oldData = new Product();

        oldData.setId(product.getId());
        oldData.setName(product.getName());
        oldData.setType(product.getType());
        oldData.setValidity(product.getValidity());
        oldData.setLot(product.getLot());
        oldData.setPrice(product.getPrice());
        oldData.setStatus(product.getStatus());

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }

        if (dto.getType() != null) {
            product.setType(dto.getType());
        }

        if (dto.getValidity() != null) {
            product.setValidity(dto.getValidity());
        }

        if (dto.getLot() != null) {
            product.setLot(dto.getLot());
        }

        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }

        Product updated = repository.save(product);

        audit.newAudit(
                user,
                company,
                "Product",
                "Update",
                oldData,
                updated
        );

        return updated;
    }

    /**
     * Retrieves a paginated list of active products belonging to the authenticated company.
     *
     * <p>Only products marked as active ({@code status = true}) are returned.
     *
     * <p>Results are cached using a pageable-aware cache key strategy in order
     * to reduce database load for repeated read operations.
     *
     * <p>Data isolation is enforced by filtering products using the authenticated
     * company context.
     *
     * @param pageable pagination and sorting configuration
     * @return paginated list of active company products
     */
    @Cacheable(
            value = "products",
            key = "@cacheKeyService.productsKey(#pageable)",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {

        Company company = authUserService.getCompanyOrThrow();

        return repository.findByCompanyAndStatusOrderByIdAsc(company, true, pageable );
    }

    /**
     * Performs a logical deletion of a {@link Product}.
     *
     * <p>The entity is not physically removed from the database.
     * Instead, its status flag is set to {@code false}, excluding it
     * from standard active queries.
     *
     * <p>The target product must belong to the authenticated user's company.
     *
     * <p>A snapshot containing the previous entity state is created before mutation
     * to support audit logging.
     *
     * <p>An audit record is generated describing the deletion event.
     *
     * <p>Flow:
     * <ol>
     *     <li>Resolve authenticated user and company</li>
     *     <li>Validate product ownership</li>
     *     <li>Capture previous state for auditing</li>
     *     <li>Mark entity as inactive</li>
     *     <li>Persist updated entity</li>
     *     <li>Create audit entry</li>
     * </ol>
     *
     * @param id product identifier
     * @return updated {@link Product} entity marked as inactive
     *
     * @throws RuntimeException if the product does not exist
     *         or does not belong to the authenticated company
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product delete(Integer id) {

        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Product product = getProductForCompany(id, company);

        Product oldData = new Product();

        oldData.setId(product.getId());
        oldData.setName(product.getName());
        oldData.setType(product.getType());
        oldData.setValidity(product.getValidity());
        oldData.setLot(product.getLot());
        oldData.setPrice(product.getPrice());
        oldData.setStatus(product.getStatus());

        product.setStatus(false);

        Product deleted = repository.save(product);

        audit.newAudit(
                user,
                company,
                "Product",
                "Delete",
                oldData,
                deleted
        );

        return deleted;
    }

    /**
     * Retrieves and validates a {@link Product} for the specified company context.
     *
     * <p>This method centralizes ownership validation logic to ensure
     * consistent multi-tenant access control across all service operations.
     *
     * <p>The product must exist and belong to the authenticated company.
     *
     * @param id product identifier
     * @param company authenticated company context
     * @return validated {@link Product} entity
     *
     * @throws RuntimeException if the product does not exist
     *         or does not belong to the provided company
     */
    private Product getProductForCompany(Integer id, Company company) {

        Product product = repository.findByIdActive(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Product does not belong to company");
        }

        return product;
    }
}
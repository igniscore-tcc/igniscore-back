package com.igniscore.api.service;

import com.igniscore.api.dto.product.ProductQueryDTO;
import com.igniscore.api.dto.product.ProductResponseDTO;
import com.igniscore.api.dto.product.ProductStoreDTO;
import com.igniscore.api.dto.product.ProductUpdateDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.service.subscription.RequiresSubscriptionAccess;
import com.igniscore.api.utils.AuditUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer responsible for lifecycle management of {@link Product} entities.
 *
 * <p>This service encapsulates product creation, updates, retrieval, and
 * logical deletion while enforcing multi-tenant isolation through the
 * authenticated user's company context.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Persisting products associated with a company</li>
 *     <li>Applying partial updates to existing products</li>
 *     <li>Performing logical deletion using the status flag</li>
 *     <li>Restricting access to company-owned resources</li>
 *     <li>Providing paginated retrieval of active products</li>
 *     <li>Generating audit records for mutating operations</li>
 *     <li>Managing cache invalidation and cached reads</li>
 * </ul>
 *
 * <p>All operations rely on the authenticated context provided by
 * {@link AuthenticatedUserService}.
 */
@RequiresSubscriptionAccess
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final AuthenticatedUserService authUserService;
    private final AuditUtils audit;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates and persists a new product associated with the authenticated company.
     *
     * <p>The product is initialized as active and linked to the authenticated
     * user's company. After persistence, the entity is refreshed to synchronize
     * its state with values generated or modified by the database.
     *
     * <p>An audit record is generated for the creation event.
     *
     * @param dto DTO containing product creation data
     * @return persisted product entity
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product store(ProductStoreDTO dto) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Product product = new Product(dto, company);
        Product saved = repository.save(product);

        entityManager.refresh(saved);

        audit.newAudit(
                user,
                company,
                "Product",
                "Create",
                null,
                saved
        );

        return saved;
    }

    /**
     * Updates an existing product using partial update semantics.
     *
     * <p>Only non-null DTO fields are applied. The product must belong to
     * the authenticated user's company. A snapshot of the previous state
     * is retained for audit logging.
     *
     * @param dto DTO containing the product identifier and update data
     * @return updated product entity
     * @throws RuntimeException if the product does not exist or does not
     *                           belong to the authenticated company
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product update(ProductUpdateDTO dto) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Product product = getProductForCompany(dto.getId(), company);
        Product oldData = new Product(product);

        product.update(dto);

        audit.newAudit(
                user,
                company,
                "Product",
                "Update",
                oldData,
                product
        );

        return product;
    }

    /**
     * Retrieves a paginated list of active products belonging to the
     * authenticated company.
     *
     * <p>Results are cached to reduce database load. The cache key must
     * include both the company identifier and pagination parameters to
     * prevent data from one tenant being served to another.
     *
     * @param pageable pagination and sorting configuration
     * @return paginated product response containing products and totals
     */
    @Cacheable(
            value = "products",
            key = "@cacheKeyService.productsKey(#pageable)",
            unless = "#result == null"
    )
    @Transactional(readOnly = true)
    public ProductQueryDTO findAll(Pageable pageable) {
        Company company = authUserService.getCompanyOrThrow();

        Page<Product> page = repository.findByCompanyAndStatusOrderByIdAsc(
                company,
                true,
                pageable
        );

        List<ProductResponseDTO> products = page.getContent()
                .stream()
                .map(ProductResponseDTO::new)
                .toList();

        return new ProductQueryDTO(
                products,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    /**
     * Logically deletes a product by setting its status to inactive.
     *
     * <p>The database record is preserved. The product must belong to
     * the authenticated company, and an audit record captures the change.
     *
     * @param id identifier of the product to deactivate
     * @return product entity marked as inactive
     * @throws RuntimeException if the product does not exist or does not
     *                           belong to the authenticated company
     */
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product delete(Integer id) {
        User user = authUserService.getUserOrThrow();
        Company company = authUserService.getCompanyOrThrow();

        Product product = getProductForCompany(id, company);
        Product oldData = new Product(product);

        product.deactivate();

        audit.newAudit(
                user,
                company,
                "Product",
                "Delete",
                oldData,
                product
        );

        return product;
    }

    /**
     * Retrieves a product and verifies that it belongs to the specified company.
     *
     * <p>Only active products can be retrieved through this method.
     *
     * @param id product identifier
     * @param company authenticated company context
     * @return product belonging to the specified company
     * @throws RuntimeException if the product does not exist, is inactive,
     *                           or belongs to another company
     */
    private Product getProductForCompany(Integer id, Company company) {
        Product product = repository.findByIdAndStatusTrue(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getCompany() == null
                || !product.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Product does not belong to company");
        }

        return product;
    }
}
package com.igniscore.api.service;

import com.igniscore.api.dto.ProductStoreDTO;
import com.igniscore.api.dto.ProductUpdateDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for managing {@link Product} entities.
 *
 * <p>This service implements core product operations including creation, update,
 * soft deletion, and retrieval, always enforcing multi-tenant isolation based on
 * the authenticated user's company context.
 *
 * <p>All business rules assume a valid authenticated user provided by
 * {@link AuthenticatedUserService}.
 *
 * <p>Responsibilities include:
 * <ul>
 *     <li>Creating products bound to a company</li>
 *     <li>Applying partial updates to products</li>
 *     <li>Performing soft deletion via status flag</li>
 *     <li>Enforcing company-level access control</li>
 *     <li>Retrieving paginated product data</li>
 * </ul>
 */
@Service
public class ProductService {

    private final ProductRepository repository;
    private final AuthenticatedUserService authUserService;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository repository responsible for product persistence
     * @param authUserService service responsible for authenticated user context resolution
     */
    public ProductService(ProductRepository repository, AuthenticatedUserService authUserService) {
        this.repository = repository;
        this.authUserService = authUserService;
    }

    @PersistenceContext
    @SuppressWarnings("unused")
    private EntityManager entityManager;

    /**
     * Creates and persists a new product associated with the authenticated user's company.
     *
     * <p>The product is initialized with active status and persisted within the current
     * persistence context. After saving, the entity state is synchronized with the database
     * using {@link EntityManager#refresh(Object)}.
     *
     * <p>Flow:
     * <ol>
     *     <li>Resolve authenticated user's company</li>
     *     <li>Map DTO data to entity</li>
     *     <li>Persist entity</li>
     *     <li>Refresh entity state from database</li>
     * </ol>
     *
     * @param dto DTO containing product creation data
     * @return persisted {@link Product} entity
     */
    @Transactional
    public Product store(ProductStoreDTO dto) {

        Company company = this.authUserService.getCompanyOrThrow();

        Product product = new Product();
        product.setName(dto.getName());
        product.setType(dto.getType());
        product.setValidity(dto.getValidity());
        product.setLot(dto.getLot());
        product.setPrice(dto.getPrice());
        product.setCompany(company);
        product.setStatus(true);

        Product saved = repository.save(product);

        entityManager.refresh(saved);

        return saved;
    }

    /**
     * Updates an existing product using partial update semantics.
     *
     * <p>Only non-null fields provided in the DTO will be applied to the entity.
     * Fields not present remain unchanged.
     *
     * <p>Access control is enforced by ensuring the product belongs to the authenticated user's company.
     *
     * <p>Flow:
     * <ol>
     *     <li>Resolve authenticated user's company</li>
     *     <li>Retrieve product ensuring company ownership</li>
     *     <li>Apply partial updates</li>
     *     <li>Persist updated entity</li>
     *     <li>Refresh entity state from database</li>
     * </ol>
     *
     * @param dto DTO containing update data and product identifier
     * @return updated {@link Product} entity
     *
     * @throws RuntimeException if product is not found or does not belong to company
     */
    @Transactional
    public Product update(ProductUpdateDTO dto) {

        Company company = authUserService.getCompanyOrThrow();
        Product product = getProductForCompany(dto.getId(), company);

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
        return repository.save(product);
    }

    /**
     * Retrieves a paginated list of active products belonging to the authenticated user's company.
     *
     * <p>Only products with {@code status = true} are returned, ensuring soft-deleted records
     * are excluded from the result set.
     *
     * <p>Data isolation is enforced at the company level.
     *
     * @param pageable pagination and sorting configuration
     * @return paginated list of products scoped to the authenticated user's company
     */
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {

        Company company = authUserService.getCompanyOrThrow();

        return repository.findByCompanyAndStatusOrderByIdAsc(company, true, pageable );
    }

    /**
     * Performs a soft deletion of a product.
     *
     * <p>The product is not physically removed from the database. Instead, its status
     * flag is set to {@code false}, marking it as inactive and excluding it from
     * standard queries.
     *
     * <p>Flow:
     * <ol>
     *     <li>Resolve authenticated user's company</li>
     *     <li>Retrieve product ensuring company ownership</li>
     *     <li>Mark product as inactive</li>
     *     <li>Persist updated entity</li>
     * </ol>
     *
     * @param id product identifier
     * @return updated {@link Product} entity marked as inactive
     *
     * @throws RuntimeException if product is not found or does not belong to company
     */
    @Transactional
    public Product delete(Integer id) {

        Company company = authUserService.getCompanyOrThrow();
        Product product = getProductForCompany(id, company);

        product.setStatus(false);

        return repository.save(product);
    }

    /**
     * Retrieves a product by ID ensuring it belongs to the specified company.
     *
     * <p>This method centralizes multi-tenant validation logic, preventing duplication
     * across service methods and ensuring consistent access control enforcement.
     *
     * @param id product identifier
     * @param company authenticated user's company
     * @return validated {@link Product} entity
     *
     * @throws RuntimeException if product is not found or does not belong to company
     */
    private Product getProductForCompany(Integer id, Company company) {

        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Product does not belong to company");
        }

        return product;
    }
}
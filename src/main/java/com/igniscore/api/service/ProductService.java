package com.igniscore.api.service;

import com.igniscore.api.dto.CompanyDTO;
import com.igniscore.api.dto.ProductDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.utils.CompanyUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service responsible for managing {@link Product} entities.
 *
 * <p>This class handles product creation, update, soft deletion and transformation into DTOs,
 * enforcing multi-tenant constraints based on the authenticated user's company.
 *
 * <p>Key responsibilities:
 * <ul>
 *     <li>Create products associated with a company</li>
 *     <li>Update existing products</li>
 *     <li>Perform soft delete (logical deletion)</li>
 *     <li>Ensure authenticated context is respected</li>
 *     <li>Map entities to DTOs for external exposure</li>
 * </ul>
 */
@Service
public class ProductService {

    private final ProductRepository repository;
    private final CompanyUtils companyUtils;
    private final AuthenticatedUserService authUserService;

    /**
     * Constructor-based dependency injection.
     *
     * @param repository         product persistence repository
     * @param companyUtils       utility for resolving company context
     * @param authUserService    service for retrieving authenticated user context safely
     */
    public ProductService(ProductRepository repository, CompanyUtils companyUtils, AuthenticatedUserService authUserService) {
        this.repository = repository;
        this.companyUtils = companyUtils;
        this.authUserService = authUserService;
    }

    /**
     * Creates a new product associated with the authenticated user's company.
     *
     * <p>Flow:
     * <ol>
     *     <li>Validate authenticated user</li>
     *     <li>Resolve company from user context</li>
     *     <li>Create and persist product</li>
     *     <li>Convert entity to DTO</li>
     * </ol>
     *
     * @param name     product name
     * @param type     product type/category
     * @param validity expiration or validity date
     * @param lot      product batch/lot identifier
     * @param price    product price
     *
     * @return product DTO representation
     *
     * @throws RuntimeException if no authenticated user is found
     */
    public ProductDTO create(String name, String type, LocalDate validity,
                             String lot, Float price) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        Product product = new Product();
        product.setName(name);
        product.setType(type);
        product.setValidity(validity);
        product.setLot(lot);
        product.setPrice(price);
        product.setCompany(company);

        Product savedProduct = repository.save(product);

        return toDTO(savedProduct);
    }

    /**
     * Converts a {@link Product} entity into a {@link ProductDTO}.
     *
     * <p>This method also maps the associated {@link Company} entity
     * into a nested {@link CompanyDTO} when present.
     *
     * @param product product entity
     * @return mapped DTO
     */
    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setType(product.getType());
        dto.setValidity(product.getValidity());
        dto.setLot(product.getLot());
        dto.setPrice(product.getPrice());

        Company company = product.getCompany();

        if (company != null) {
            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(company.getId());
            companyDTO.setName(company.getName());
            companyDTO.setCnpj(company.getCnpj());
            companyDTO.setIe(company.getIe());
            companyDTO.setUfIe(company.getUfIe());
            companyDTO.setEmail(company.getEmail());
            companyDTO.setPhone(company.getPhone());

            dto.setCompany(companyDTO);
        }

        return dto;
    }

    /**
     * Performs a soft delete (logical deletion) of a product.
     *
     * <p>Instead of removing the record from the database, this method sets
     * the product status to inactive (false).
     *
     * <p>Flow:
     * <ol>
     *     <li>Retrieve authenticated user's company</li>
     *     <li>Fetch product by ID</li>
     *     <li>Validate ownership (multi-tenant check)</li>
     *     <li>Mark product as inactive</li>
     *     <li>Persist changes</li>
     * </ol>
     *
     * @param id product identifier
     * @return updated product entity (inactive)
     *
     * @throws RuntimeException if product is not found
     * @throws RuntimeException if product does not belong to the user's company
     */
    public Product delete(Integer id) {
        Company company = authUserService.getCompanyOrThrow();

        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("Product not valid");
        }

        product.setStatus(false);

        return repository.save(product);
    }

    /**
     * Updates an existing product.
     *
     * <p>This method performs a partial update, meaning only non-null fields
     * will be updated.
     *
     * <p>Flow:
     * <ol>
     *     <li>Validate authenticated user</li>
     *     <li>Resolve company context</li>
     *     <li>Apply provided fields to product</li>
     *     <li>Persist updated entity</li>
     *     <li>Convert to DTO</li>
     * </ol>
     *
     * <p><b>Important:</b> This implementation creates a new instance of {@link Product}
     * instead of fetching the existing one, which may overwrite fields unintentionally
     * if not handled carefully.
     *
     * @param id       product identifier
     * @param name     product name (optional)
     * @param type     product type/category (optional)
     * @param validity expiration or validity date (optional)
     * @param lot      product batch/lot identifier (optional)
     * @param price    product price (optional)
     *
     * @return updated product DTO
     *
     * @throws RuntimeException if no authenticated user is found
     */
    @Transactional
    public ProductDTO update(Integer id, String name, String type, LocalDate validity,
                             String lot, Float price) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setId(id);
        if(name != null) product.setName(name);
        if(type != null) product.setType(type);
        if(validity != null) product.setValidity(validity);
        if(lot != null) product.setLot(lot);
        if(price != null) product.setPrice(price);
        if(company != null) product.setCompany(company);

        Product savedProduct = repository.save(product);

        return toDTO(savedProduct);
    }

    /**
     * Retrieves a paginated list of products belonging to the authenticated user's company.
     *
     * <p>This method ensures data isolation by filtering products based on the company
     * associated with the currently authenticated user.</p>
     *
     * <p>The transaction is marked as read-only to optimize performance and prevent
     * unintended data modifications.</p>
     *
     * @param pageable the pagination and sorting information (page number, page size, sort order)
     * @return a {@link org.springframework.data.domain.Page} containing products for the user's company
     *
     * @throws RuntimeException if the authenticated user is not associated with any company
     */
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        Company company = authUserService.getCompanyOrThrow();
        return repository.findByCompany(company, pageable);
    }
}
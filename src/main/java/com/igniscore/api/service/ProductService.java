package com.igniscore.api.service;

import com.igniscore.api.dto.CompanyDTO;
import com.igniscore.api.dto.ProductDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.Product;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.utils.CompanyUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service responsible for managing {@link Product} entities.
 *
 * <p>This class handles product creation and transformation into DTOs,
 * enforcing multi-tenant constraints based on the authenticated user's company.
 *
 * <p>Key responsibilities:
 * <ul>
 *     <li>Create products associated with a company</li>
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
     * @param repository   product persistence repository
     * @param companyUtils utility for resolving company context
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
    public ProductDTO createProduct(String name, String type, LocalDate validity,
                                    String lot, Float price) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure a valid authenticated user exists
        if (authentication == null || !(authentication.getPrincipal() instanceof User loggedUser)) {
            throw new RuntimeException("No authenticated user found");
        }

        // Resolve company in a multi-tenant safe way
        Company company = companyUtils.loggedCompany(loggedUser.getCompany().getId());

        // Create product entity
        Product product = new Product();
        product.setName(name);
        product.setType(type);
        product.setValidity(validity);
        product.setLot(lot);
        product.setPrice(price);
        product.setCompany(company);

        // Persist entity
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

        // Map company if present
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


    public Product delete(Integer id) {
        Company company = authUserService.getCompanyOrThrow();

        Product product = repository.findByCompany(company);

        if(product == null) {
            throw new RuntimeException("Product not found");
        }

        product.setStatus(false);

        return  repository.save(product);

    }
}
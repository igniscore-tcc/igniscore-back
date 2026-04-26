package com.igniscore.api.controller;

import com.igniscore.api.dto.ProductStoreDTO;
import com.igniscore.api.dto.ProductUpdateDTO;
import com.igniscore.api.model.Product;
import com.igniscore.api.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * GraphQL controller responsible for handling product-related operations.
 *
 * <p>This controller acts as the API layer between GraphQL requests and the
 * {@link ProductService}, exposing mutations and queries for product management.
 *
 * <p>It supports:
 * <ul>
 *     <li>Product creation</li>
 *     <li>Product update (partial update via DTO)</li>
 *     <li>Product soft deletion</li>
 *     <li>Paginated product listing</li>
 * </ul>
 *
 * <p>All operations are scoped to the authenticated user's company through the service layer.
 */
@Controller
@SuppressWarnings("unused")
public class ProductController {

    private final ProductService service;

    /**
     * Constructor-based dependency injection.
     *
     * @param service product business service
     */
    public ProductController(ProductService service) {
        this.service = service;
    }

    /**
     * GraphQL mutation responsible for creating a new product.
     *
     * <p>Delegates creation logic to {@link ProductService#store(ProductStoreDTO)}.
     *
     * @param input DTO containing product creation data
     * @return persisted {@link Product} entity
     */
    @MutationMapping
    @SuppressWarnings("unused")
    public Product storeProduct(@Argument ProductStoreDTO input) {
        return service.store(input);
    }

    /**
     * GraphQL mutation responsible for updating an existing product.
     *
     * <p>Supports partial updates based on non-null fields in {@link ProductUpdateDTO}.
     *
     * @param input DTO containing update data and product identifier
     * @return updated {@link Product} entity
     */
    @MutationMapping
    @SuppressWarnings("unused")
    public Product updateProduct(@Argument ProductUpdateDTO input) {
        return service.update(input);
    }

    /**
     * GraphQL query responsible for retrieving paginated products.
     *
     * <p>Supports optional pagination parameters. Defaults:
     * <ul>
     *     <li>page = 0</li>
     *     <li>size = 10</li>
     * </ul>
     *
     * <p>Only products belonging to the authenticated user's company are returned,
     * and only active products (status = true) are included.
     *
     * @param page page index (zero-based)
     * @param size number of items per page
     * @return list of {@link Product} entities for the requested page
     */
    @QueryMapping
    public List<Product> products(@Argument Integer page, @Argument Integer size) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        return service.findAll(pageable).getContent();
    }

    /**
     * GraphQL mutation responsible for performing a soft delete on a product.
     *
     * <p>The product is not physically removed from the database. Instead,
     * its status is set to inactive.
     *
     * @param id product identifier
     * @return updated {@link Product} entity marked as inactive
     */
    @MutationMapping
    @SuppressWarnings("unused")
    public Product deleteProduct(@Argument Integer id) {
        return service.delete(id);
    }
}
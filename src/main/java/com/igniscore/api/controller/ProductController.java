package com.igniscore.api.controller;

import com.igniscore.api.dto.product.ProductQueryDTO;
import com.igniscore.api.dto.product.ProductStoreDTO;
import com.igniscore.api.dto.product.ProductUpdateDTO;
import com.igniscore.api.model.Product;
import com.igniscore.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

/**
 * GraphQL controller responsible for handling product-related operations.
 *
 * <p>Delegates product creation, updates, retrieval, and logical deletion
 * to {@link ProductService}. Access to product data is scoped to the
 * authenticated user's company by the service layer.
 */
@Controller
@RequiredArgsConstructor
public class ProductController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    private final ProductService service;

    /**
     * Creates a new product.
     *
     * @param input DTO containing product registration data
     * @return created product
     */
    @MutationMapping
    public Product storeProduct(@Argument ProductStoreDTO input) {
        return service.store(input);
    }

    /**
     * Updates an existing product using the provided non-null fields.
     *
     * <p>Only users with the OWNER or ADMIN role can perform this operation.
     *
     * @param input DTO containing the product identifier and update data
     * @return updated product
     */
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @MutationMapping
    public Product updateProduct(@Argument ProductUpdateDTO input) {
        return service.update(input);
    }

    /**
     * Retrieves a paginated list of active products belonging to the
     * authenticated user's company.
     *
     * <p>Default pagination values are page 0 and 10 items per page.
     * Results are sorted by product identifier in ascending order.
     *
     * @param page zero-based page index, or {@code null} for the default
     * @param size number of items per page, or {@code null} for the default
     * @return paginated product results
     */
    @QueryMapping
    public ProductQueryDTO products(
            @Argument Integer page,
            @Argument Integer size
    ) {
        Pageable pageable = PageRequest.of(
                page != null ? page : DEFAULT_PAGE,
                size != null ? size : DEFAULT_SIZE,
                Sort.by(Sort.Direction.ASC, "id")
        );

        return service.findAll(pageable);
    }

    /**
     * Logically deletes a product by marking it as inactive.
     *
     * <p>Only users with the OWNER or ADMIN role can perform this operation.
     *
     * @param id identifier of the product to deactivate
     * @return product marked as inactive
     */
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    @MutationMapping
    public Product deleteProduct(@Argument Integer id) {
        return service.delete(id);
    }
}
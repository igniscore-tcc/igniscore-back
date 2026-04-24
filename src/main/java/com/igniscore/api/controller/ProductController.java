package com.igniscore.api.controller;

import com.igniscore.api.dto.ProductDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Product;
import com.igniscore.api.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsible for handling GraphQL mutations related to products.
 * This class acts as an entry point for client requests that intend to create
 * new products. It delegates the business logic to the ProductService layer.
 */
@Controller
public class ProductController {

    /**
     * Service responsible for product-related business operations.
     */
    private final ProductService service;

    /**
     * Constructor for ProductController.
     *
     * @param service the ProductService instance used to handle business logic
     */
    public ProductController(ProductService service){
        this.service = service;
    }

    /**
     * GraphQL mutation used to create a new product.
     * This method receives product data as arguments from the GraphQL request
     * and forwards them to the service layer for processing and persistence.
     *
     * @param name     the name of the product
     * @param type     the type/category of the product
     * @param validity the expiration or validity date of the product
     * @param lot      the batch or lot identifier of the product
     * @param price    the price of the product
     * @return a ProductDTO containing the created product data
     */
    @MutationMapping
    public ProductDTO createProduct(@Argument String name,
                                    @Argument String type,
                                    @Argument LocalDate validity,
                                    @Argument String lot,
                                    @Argument Float price) {
        return service.create(name, type, validity, lot, price);
    }

    /**
     * GraphQL mutation used to delete a product by its identifier.
     *
     * @param id the unique identifier of the product to be deleted
     * @return the deleted Product entity
     */
    @MutationMapping
    public Product deleteProduct(@Argument Integer id) {
        return service.delete(id);
    }

    /**
     * GraphQL mutation used to update an existing product.
     * All provided fields will be forwarded to the service layer for update processing.
     *
     * @param id       the unique identifier of the product to be updated
     * @param name     the updated name of the product
     * @param type     the updated type/category of the product
     * @param validity the updated validity or expiration date
     * @param lot      the updated batch or lot identifier
     * @param price    the updated price of the product
     * @return a ProductDTO containing the updated product data
     */
    @MutationMapping
    public ProductDTO updateProduct(@Argument Integer id,
                                    @Argument String name,
                                    @Argument String type,
                                    @Argument LocalDate validity,
                                    @Argument String lot,
                                    @Argument Float price) {

        return service.update(id, name, type, validity, lot, price);
    };

    /**
     * GraphQL query used to retrieve a paginated list of products.
     *
     * <p>If pagination parameters are not provided, default values are applied:
     * page = 0 and size = 10.</p>
     *
     * @param page the page number (zero-based index)
     * @param size the number of items per page
     * @return a list of products for the requested page
     */
    @QueryMapping
    @SuppressWarnings("unused")
    public List<Product> products(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10
        );

        return service.findAll(pageable).getContent();
    }
}
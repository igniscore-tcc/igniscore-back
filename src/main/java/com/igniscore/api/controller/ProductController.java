package com.igniscore.api.controller;

import com.igniscore.api.dto.ProductDTO;
import com.igniscore.api.model.Product;
import com.igniscore.api.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

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
        return service.createProduct(name, type, validity, lot, price);
    }


    @MutationMapping
    public Product deleteProduct(@Argument Integer id) {
        return service.delete(id);
    }
}
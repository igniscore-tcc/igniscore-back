package com.igniscore.api.controller;

import com.igniscore.api.dto.ProductDTO;
import com.igniscore.api.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @MutationMapping
    public ProductDTO createProduct(@Argument String name,
                                    @Argument String type,
                                    @Argument LocalDate validity,
                                    @Argument String lot,
                                    @Argument Float price) {
        return service.createProduct(name, type, validity, lot, price);
    }
}
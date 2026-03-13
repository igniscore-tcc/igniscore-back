package com.igniscore.api.controller;

import com.igniscore.api.model.Product;
import com.igniscore.api.repository.ProductRepository;
import com.igniscore.api.service.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @MutationMapping
    public Product createProduct(@Argument String name, @Argument String type, @Argument Date validity, @Argument String lot, @Argument Float price){
        return service.createProduct(name, type, validity, lot, price);
    }
}

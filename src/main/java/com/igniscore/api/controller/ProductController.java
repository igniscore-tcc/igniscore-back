package com.igniscore.api.controller;

import com.igniscore.api.dto.ProductDTO;
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

@Controller
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @MutationMapping
    public ProductDTO storeProduct(@Argument String name, @Argument String type, @Argument LocalDate validity,
                                   @Argument String lot, @Argument Float price) {
        return  service.create(name, type, validity, lot, price);
    }

    @MutationMapping
    public ProductDTO updateProduct(@Argument Integer id, @Argument String name, @Argument String type, @Argument LocalDate validity,
                                    @Argument String lot, @Argument Float price) {
        return  service.update(id, name, type, validity, lot, price);
    }

    @QueryMapping
    public List<Product> products(@Argument Integer page, @Argument Integer size) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 10
        );

        return service.findAll(pageable).getContent();
    }

    @MutationMapping
    public Product deleteProduct(@Argument Integer id) {
        return service.delete(id);
    }

}
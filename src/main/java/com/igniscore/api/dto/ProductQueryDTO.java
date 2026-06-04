package com.igniscore.api.dto;

import com.igniscore.api.model.Product;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ProductQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<Product> products;

    private final int totalPages;

    private final Long totalProducts;


    public ProductQueryDTO(List<Product> products, int totalPages, Long totalProducts) {
        this.products = products;
        this.totalPages = totalPages;
        this.totalProducts = totalProducts;
    }

    public List<Product> getProducts() {
        return products;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }
}

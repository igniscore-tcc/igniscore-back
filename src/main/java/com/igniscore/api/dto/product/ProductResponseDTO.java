package com.igniscore.api.dto.product;

import com.igniscore.api.dto.company.CompanyResponseDTO;
import com.igniscore.api.model.Product;
import com.igniscore.api.model.ProductType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductResponseDTO(
        Integer id,
        Integer numberProduct,
        String name,
        ProductType type,
        LocalDate validity,
        String lot,
        BigDecimal price,
        Boolean status,
        CompanyResponseDTO company
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductResponseDTO(Product product) {
        this(
                product.getId(),
                product.getNumberProduct(),
                product.getName(),
                product.getType(),
                product.getValidity(),
                product.getLot(),
                product.getPrice(),
                product.getStatus(),
                product.getCompany() != null ? new CompanyResponseDTO(product.getCompany()) : null
        );
    }
}

/*
type ProductOutput {
    id: ID!
    numberProduct: Int
    name: String!
    type: String!
    validity: Date!
    lot: String!
    price: BigDecimal!
    company: Company
}
 */

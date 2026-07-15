package com.igniscore.api.dto.sale;

import com.igniscore.api.dto.product.ProductResponseDTO;
import com.igniscore.api.model.SaleItem;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public record SaleItemResponseDTO(
        Integer id ,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal total,
        ProductResponseDTO product
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public SaleItemResponseDTO(SaleItem item) {
        this(
                item.getId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotal(),
                item.getProduct() != null ? new ProductResponseDTO(item.getProduct()) : null
        );
    }
}

/*
type SaleItem {
    id: ID!
    quantity: Int!
    unitPrice: BigDecimal!
    total: BigDecimal!
    product: Product!
}
 */
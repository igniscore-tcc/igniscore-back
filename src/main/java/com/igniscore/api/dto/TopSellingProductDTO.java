package com.igniscore.api.dto;

public record TopSellingProductDTO(
        Integer productId,
        String productName,
        Long totalSold
) {
}
package com.igniscore.api.dto.dashboard;

public record TopSellingProductDTO(
        Integer productId,
        String productName,
        Long totalSold
) {
}
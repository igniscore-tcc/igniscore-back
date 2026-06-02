package com.igniscore.api.dto;

import java.math.BigDecimal;

public record SalesByClientDTO(
        Integer clientId,
        String clientName,
        BigDecimal totalSales
) {
}
package com.igniscore.api.dto.sale;

import java.math.BigDecimal;

public record SalesByClientDTO(
        Integer clientId,
        String clientName,
        BigDecimal totalSales
) {
}
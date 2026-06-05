package com.igniscore.api.dto.dashboard;

import java.math.BigDecimal;

public record SalesByClientDTO(
        Integer clientId,
        String clientName,
        BigDecimal totalSales
) {
}
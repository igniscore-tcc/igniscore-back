package com.igniscore.api.dto;

import java.math.BigDecimal;

public record MonthlySalesDTO(
        Integer month,
        BigDecimal total
) {
}
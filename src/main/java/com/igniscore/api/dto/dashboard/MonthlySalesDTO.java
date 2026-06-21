package com.igniscore.api.dto.dashboard;

import java.math.BigDecimal;

public record MonthlySalesDTO(
        Integer month,
        Integer year,
        BigDecimal total
) {
}
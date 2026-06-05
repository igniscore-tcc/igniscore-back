package com.igniscore.api.dto.expiration;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpirationProjectionDTO(
        Integer saleId,
        String clientName,
        LocalDate saleDate,
        LocalDate dueDate,
        BigDecimal totalSale
) {}

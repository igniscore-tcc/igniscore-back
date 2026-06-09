package com.igniscore.api.dto;

import com.igniscore.api.model.ExpirationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpirationDTO(
        Integer saleId,
        String clientName,
        LocalDate saleDate,
        LocalDate dueDate,
        BigDecimal totalSale,
        ExpirationStatus status
) {}
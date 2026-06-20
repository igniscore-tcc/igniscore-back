package com.igniscore.api.dto.expiration;

import com.igniscore.api.model.ExpirationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpirationDTO(
        Integer expirationId,
        Integer expirationNumber,
        Integer saleId,
        String clientName,
        LocalDate saleDate,
        LocalDate dueDate,
        BigDecimal totalSale,
        ExpirationStatus status
) {}
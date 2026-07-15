package com.igniscore.api.dto.expiration;

import com.igniscore.api.model.Expiration;
import com.igniscore.api.model.ExpirationStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpirationResponseDTO(
    Integer expirationId,
    Integer expirationNumber,
    Integer saleId,
    String clientName,
    LocalDate saleDate,
    LocalDate dueDate,
    BigDecimal totalSale,
    ExpirationStatus status
) implements Serializable {

    private static final long serialSessionUID = 1L;

    public ExpirationResponseDTO(Expiration expiration) {
        this(
                expiration.getId(),
                expiration.getNumberExpiration(),
                expiration.getSale().getId(),
                expiration.getSale().getClient().getName(),
                expiration.getSale().getDate(),
                expiration.getSale().getDueDate(),
                expiration.getSale().getTotal(),
                expiration.getStatus()
        );
    }
}

/*
type Expiration {
    expirationId: Int!
    expirationNumber: Int!
    saleId: Int!
    clientName: String!
    saleDate: Date!
    dueDate: Date!
    totalSale: BigDecimal!
    status: ExpirationStatus!
}
 */

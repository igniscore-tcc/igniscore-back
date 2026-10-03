package com.igniscore.api.dto.sale;

import com.igniscore.api.dto.client.ClientResponseDTO;
import com.igniscore.api.model.SaleType;
import com.igniscore.api.model.Sale;
import com.igniscore.api.model.SaleDocument;
import com.igniscore.api.model.SaleStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record SaleResponseDTO(
    Integer id,
    Integer numberSale,
    Integer quantityItems,
    BigDecimal discount,
    BigDecimal total,
    LocalDate date,
    SaleType paymentMethod,
    SaleStatus status,
    SaleDocument type,
    String document,
    LocalDate dueDate,
    ClientResponseDTO client,
    List<SaleItemResponseDTO> items
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public SaleResponseDTO(Sale sale) {
        this(
                sale.getId(),
                sale.getNumberSale(),
                sale.getQuantityItems(),
                sale.getDiscount(),
                sale.getTotal(),
                sale.getDate(),
                sale.getPaymentMethod(),
                sale.getStatus(),
                sale.getType(),
                sale.getDocument(),
                sale.getDueDate(),
                sale.getClient() != null ? new ClientResponseDTO(sale.getClient()) : null,
                sale.getItems() != null
                        ? sale.getItems().stream().map(SaleItemResponseDTO::new).toList()
                        : Collections.emptyList()
        );
    }
}

/*
type Sale {
    id: ID!
    numberSale: Int!
    quantityItems: Int!
    discount: BigDecimal
    total: BigDecimal!
    date: Date!
    paymentMethod: PaymentMethod!
    status: SaleStatus!
    dueDate: Date!
    client: Client!
    items: [SaleItem!]!
}
 */

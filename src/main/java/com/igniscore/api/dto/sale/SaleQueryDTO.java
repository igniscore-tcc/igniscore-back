package com.igniscore.api.dto.sale;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record SaleQueryDTO(List<SaleResponseDTO> sales, int totalPages, long totalSales) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}

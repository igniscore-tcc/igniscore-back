package com.igniscore.api.dto.product;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ProductQueryDTO(List<ProductResponseDTO> products, int totalPages,
                              Long totalProducts) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}

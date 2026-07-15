package com.igniscore.api.dto.expiration;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record ExpirationPageDTO(
        List<ExpirationDTO> items,
        long totalItems,
        int totalPages,
        int currentPage,
        int pageSize
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
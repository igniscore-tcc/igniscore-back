package com.igniscore.api.dto.expiration;

import java.util.List;

public record ExpirationPageDTO(
        List<ExpirationDTO> items,
        long totalItems,
        int totalPages,
        int currentPage,
        int pageSize
) {}
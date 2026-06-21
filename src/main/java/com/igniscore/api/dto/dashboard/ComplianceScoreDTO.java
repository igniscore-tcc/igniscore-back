package com.igniscore.api.dto.dashboard;

public record ComplianceScoreDTO(
        Long activeInTime,
        Long totalItens,
        Double percentage
) {}

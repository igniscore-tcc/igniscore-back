package com.igniscore.api.dto.dashboard;

import java.time.LocalDate;

public record UpcomingEquipmentExpirationDTO(
        String equipmentName,
        String location,
        Long daysRemaining,
        LocalDate expirationDate
) {}

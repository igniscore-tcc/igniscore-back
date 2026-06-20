package com.igniscore.api.dto.user;

import com.igniscore.api.model.UserRole;

public record MeDTO(
        Integer id,
        String email,
        UserRole role,
        Integer companyId
) {}
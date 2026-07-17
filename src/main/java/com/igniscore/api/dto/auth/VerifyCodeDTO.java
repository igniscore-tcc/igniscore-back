package com.igniscore.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyCodeDTO(
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, max = 6)
        String code
) {}

package com.igniscore.api.dto.stripe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeResponseDTO {

    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;
}

package com.igniscore.api.dto.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripeRequestDTO {
    private Long amount;
    private Long quantity;
    private String name;
    private String currency;
}

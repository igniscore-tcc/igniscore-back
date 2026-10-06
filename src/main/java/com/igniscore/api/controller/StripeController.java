package com.igniscore.api.controller;

import com.igniscore.api.dto.stripe.StripeRequestDTO;
import com.igniscore.api.dto.stripe.StripeResponseDTO;
import com.igniscore.api.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StripeController {

    private final SubscriptionService subscriptionService;

    @MutationMapping
    public StripeResponseDTO createCheckout(
            @Argument StripeRequestDTO input
    ) {
        return subscriptionService.createCheckout(input);
    }
}
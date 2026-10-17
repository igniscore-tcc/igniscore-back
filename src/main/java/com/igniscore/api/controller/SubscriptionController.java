package com.igniscore.api.controller;

import com.igniscore.api.dto.subscription.SubscriptionDTO;
import com.igniscore.api.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @QueryMapping
    public SubscriptionDTO mySubscription() {
        return subscriptionService.getMySubscription();
    }
}

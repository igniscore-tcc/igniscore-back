package com.igniscore.api.dto.subscription;

import java.time.LocalDateTime;

public record SubscriptionDTO(
        Long id,
        String status,
        Boolean cancelAtPeriodEnd,
        String currentPeriodStart,
        String currentPeriodEnd,
        String planName,
        Float amount,
        String currency,
        String interval
) {
    public static SubscriptionDTO from(
            com.igniscore.api.model.Subscription subscription
    ) {
        var planPrice = subscription.getPlanPrice();

        return new SubscriptionDTO(
                subscription.getId(),
                subscription.getStatus().name(),
                Boolean.TRUE.equals(subscription.getCancelAtPeriodEnd()),
                formatDate(subscription.getCurrentPeriodStart()),
                formatDate(subscription.getCurrentPeriodEnd()),
                planPrice.getPlan().getName(),
                planPrice.getAmount()
                        .movePointRight(2)
                        .floatValue(),
                planPrice.getCurrency(),
                "MONTH"
        );
    }

    private static String formatDate(LocalDateTime date) {
        return date == null ? null : date.toString();
    }
}
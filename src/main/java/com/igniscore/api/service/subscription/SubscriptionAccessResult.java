package com.igniscore.api.service.subscription;

import java.time.LocalDateTime;

public record SubscriptionAccessResult(
        SubscriptionAccessStatus status,
        String message,
        LocalDateTime gracePeriodEndsAt
) {
    public boolean canAccessBusinessFeatures() {
        return status != SubscriptionAccessStatus.BLOCKED;
    }
}
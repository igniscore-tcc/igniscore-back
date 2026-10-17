package com.igniscore.api.service.subscription;

public class SubscriptionAccessDeniedException
        extends RuntimeException {

    public SubscriptionAccessDeniedException(String message) {
        super(message);
    }
}
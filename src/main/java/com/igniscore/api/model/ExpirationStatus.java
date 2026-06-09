package com.igniscore.api.model;

public enum ExpirationStatus {
    EXPIRED("expired"),
    NEXT("next"),
    NORMAL("normal");

    private final String status;

    ExpirationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
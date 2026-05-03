package com.igniscore.api.model;

public enum SaleStatus
{
    PENDING("pending"),
    CANCELED("canceled"),
    COMPLETED("completed");

    private final String status;

    SaleStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return status;
    }
}

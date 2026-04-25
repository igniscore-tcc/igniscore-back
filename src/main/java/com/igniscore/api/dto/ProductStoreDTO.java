package com.igniscore.api.dto;

import com.igniscore.api.model.ProductType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class ProductStoreDTO {

    /**
     * Product name.
     *
     * <p>Must not be null or blank.
     */
    @NotBlank(message = "Product name is required")
    private String name;

    /**
     * Product type.
     *
     * <p>Must not be null.
     */
    @NotNull(message = "Product type is required")
    private ProductType type;

    /**
     * Product expiration date.
     *
     * <p>Must be a future date.
     */
    @NotNull(message = "Validity date is required")
    @Future(message = "Validity date must be in the future")
    private LocalDate validity;

    /**
     * Product batch/lot number.
     *
     * <p>Must not be null or blank.
     */
    @NotBlank(message = "Lot is required")
    private String lot;

    /**
     * Product sale price.
     *
     * <p>Must be greater than zero.
     */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Float price;

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public String getLot() {
        return lot;
    }

    public Float getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}

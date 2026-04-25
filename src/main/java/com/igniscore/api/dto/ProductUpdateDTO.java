package com.igniscore.api.dto;

import com.igniscore.api.model.ProductType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used for product update requests.
 *
 * <p>Supports partial updates, meaning all fields except the identifier
 * are optional. Only non-null values should be applied.
 *
 * <p><strong>Validation strategy:</strong>
 * <ul>
 *     <li>Product ID is mandatory</li>
 *     * <li>Provided fields must respect domain constraints</li>
 * </ul>
 *
 * <p><strong>Notes:</strong>
 * <ul>
 *     <li>Null fields are ignored during update</li>
 *     <li>Expiration date, when provided, must be in the future</li>
 *     <li>Price, when provided, must be greater than zero</li>
 * </ul>
 */
public class ProductUpdateDTO {

    /**
     * Product identifier.
     *
     * <p>Required for locating the product to update.
     */
    @NotNull(message = "Product ID is required")
    private Integer id;

    /**
     * Product name.
     *
     * <p>Optional field.
     */
    private String name;

    /**
     * Product type.
     *
     * <p>Optional field.
     */
    private ProductType type;

    /**
     * Product expiration date.
     *
     * <p>Optional. Must be a future date when provided.
     */
    @Future(message = "Validity date must be in the future")
    private LocalDate validity;

    /**
     * Product batch/lot number.
     *
     * <p>Optional field.
     */
    private String lot;

    /**
     * Product sale price.
     *
     * <p>Optional. Must be greater than zero when provided.
     */
    @Positive(message = "Price must be greater than zero")
    private Float price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
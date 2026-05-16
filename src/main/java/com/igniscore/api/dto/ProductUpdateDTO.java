package com.igniscore.api.dto;

import com.igniscore.api.model.ProductType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used for product update operations.
 *
 * <p>This DTO supports partial update semantics, allowing clients
 * to send only the fields that should be modified.
 *
 * <p>All attributes are optional except the product identifier,
 * which is required to locate the target entity.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Transport update data between client and application layers</li>
 *     <li>Support partial entity modification</li>
 *     <li>Apply validation rules to provided values</li>
 * </ul>
 *
 * <p>Validation strategy:
 * <ul>
 *     <li>The product identifier is mandatory</li>
 *     <li>Optional fields are validated only when present</li>
 *     <li>Null fields are ignored during update processing</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Uses {@link LocalDate} for date-only values</li>
 *     <li>Uses {@link BigDecimal} for monetary precision</li>
 *     <li>Intended exclusively for update operations</li>
 * </ul>
 */
public class ProductUpdateDTO {
    /**
     * Unique identifier of the product to be updated.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must not be null</li>
     * </ul>
     */
    @NotNull(message = "Product ID is required")
    private Integer id;
    /**
     * Commercial or display name of the product.
     *
     * <p>Optional field used in partial update operations.
     *
     * <p>When {@code null}, the current value remains unchanged.
     */
    private String name;
    /**
     * Product classification or category.
     *
     * <p>Optional field used in partial update operations.
     *
     * <p>When {@code null}, the current value remains unchanged.
     */
    private ProductType type;
    /**
     * Product expiration or validity date.
     *
     * <p>Optional field used in partial update operations.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must represent a future date when provided</li>
     * </ul>
     *
     * <p>When {@code null}, the current value remains unchanged.
     */
    @Future(message = "Validity date must be in the future")
    private LocalDate validity;
    /**
     * Product batch or lot identifier used for traceability.
     *
     * <p>Optional field used in partial update operations.
     *
     * <p>When {@code null}, the current value remains unchanged.
     */
    private String lot;
    /**
     * Monetary value associated with the product.
     *
     * <p>Optional field used in partial update operations.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must be greater than zero when provided</li>
     * </ul>
     *
     * <p>Uses {@link BigDecimal} to preserve decimal precision
     * required for financial calculations.
     *
     * <p>When {@code null}, the current value remains unchanged.
     */
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

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

    public BigDecimal getPrice() {
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

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
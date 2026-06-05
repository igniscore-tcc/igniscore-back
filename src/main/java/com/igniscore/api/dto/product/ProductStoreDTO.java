package com.igniscore.api.dto.product;

import com.igniscore.api.model.ProductType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used for product creation requests.
 *
 * <p>This DTO encapsulates the data required to create a new product
 * through the API layer.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Transport product creation data between client and application</li>
 *     <li>Apply declarative validation rules</li>
 *     <li>Provide a persistence-independent request structure</li>
 * </ul>
 *
 * <p>Validation rules are enforced using Jakarta Bean Validation annotations,
 * ensuring that invalid payloads are rejected before reaching the business layer.
 *
 * <p>Design notes:
 * <ul>
 *     <li>Uses {@link LocalDate} for date-only values</li>
 *     <li>Uses {@link BigDecimal} for monetary precision</li>
 *     <li>Intended exclusively for create operations</li>
 * </ul>
 */
public class ProductStoreDTO {
    /**
     * Commercial or display name of the product.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must not be null</li>
     *     <li>Must not be blank</li>
     * </ul>
     */
    @NotBlank(message = "Product name is required")
    private String name;
    /**
     * Product classification or category.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must not be null</li>
     * </ul>
     */
    @NotNull(message = "Product type is required")
    private ProductType type;
    /**
     * Product expiration or validity date.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must not be null</li>
     *     <li>Must represent a future date</li>
     * </ul>
     *
     * <p>Uses {@link LocalDate} because time-zone information
     * is not required for this domain attribute.
     */
    @NotNull(message = "Validity date is required")
    @Future(message = "Validity date must be in the future")
    private LocalDate validity;
    /**
     * Product batch or lot identifier used for traceability.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must not be null</li>
     *     <li>Must not be blank</li>
     * </ul>
     */
    @NotBlank(message = "Lot is required")
    private String lot;
    /**
     * Monetary value associated with the product.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Must not be null</li>
     *     <li>Must be greater than zero</li>
     * </ul>
     *
     * <p>Uses {@link BigDecimal} to preserve decimal precision
     * required for financial calculations.
     */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

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

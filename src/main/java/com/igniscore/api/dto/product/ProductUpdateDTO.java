package com.igniscore.api.dto.product;

import com.igniscore.api.model.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used for product update operations.
 *
 * <p>Encapsulates the information required to update an existing product,
 * supporting partial updates in which only the provided fields are modified.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Identify the product to be updated</li>
 *     <li>Transport modification data between the API and application layers</li>
 *     <li>Allow optional attributes to retain their current values when null</li>
 * </ul>
 *
 * <p>Design considerations:
 * <ul>
 *     <li>{@link LocalDate} represents the product validity date</li>
 *     <li>{@link BigDecimal} preserves precision for monetary values</li>
 *     <li>Business rules are handled by the application layer</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateDTO {

    /**
     * Unique identifier of the product to be updated.
     */
    private Integer id;

    /**
     * Commercial or display name of the product.
     */
    private String name;

    /**
     * Classification or category of the product.
     */
    private ProductType type;

    /**
     * Expiration or validity date of the product.
     */
    private LocalDate validity;

    /**
     * Batch or lot identifier used for product traceability.
     */
    private String lot;

    /**
     * Monetary value associated with the product.
     */
    private BigDecimal price;
}
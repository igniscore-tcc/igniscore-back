package com.igniscore.api.dto.product;

import com.igniscore.api.model.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) used for product creation requests.
 *
 * <p>Encapsulates the information required to register a product through
 * the API, keeping request data independent of the persistence layer.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Transport product creation data between the API and application layers</li>
 *     <li>Represent product attributes using appropriate domain types</li>
 *     <li>Provide accessors and a no-argument constructor through Lombok</li>
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
public class ProductStoreDTO {

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

    /**
     * Creates a product registration request with the provided attributes.
     *
     * @param name product name
     * @param type product classification
     * @param validity product validity date
     * @param lot product batch or lot identifier
     * @param price product price
     */
    public ProductStoreDTO(
            String name,
            ProductType type,
            LocalDate validity,
            String lot,
            BigDecimal price
    ) {
        this.name = name;
        this.type = type;
        this.validity = validity;
        this.lot = lot;
        this.price = price;
    }
}
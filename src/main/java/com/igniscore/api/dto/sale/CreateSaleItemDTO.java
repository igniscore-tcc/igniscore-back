package com.igniscore.api.dto.sale;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object responsible for representing
 * a sale item during the sale creation process.
 *
 * <p>This DTO is used as part of the sale creation payload,
 * containing product reference information, quantity,
 * and unit price values.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Transport product data between API and service layers</li>
 *     <li>Represent individual sale items</li>
 *     <li>Provide pricing and quantity information</li>
 * </ul>
 */
@Setter
@Getter
public class CreateSaleItemDTO {

    /**
     * Identifier of the product associated with the sale item.
     * -- GETTER --
     *  Returns the product identifier.
     * -- SETTER --
     *  Defines the product identifier.
     */
    private Integer productId;

    /**
     * Quantity of the product being sold.
     * -- GETTER --
     *  Returns the quantity of items.
     */
    private Integer quantity;

    /**
     * Unit price applied to the product at the time of sale.
     *
     * <p>{@link BigDecimal} is used to preserve
     * monetary precision and avoid floating-point inaccuracies.
     * -- GETTER --
     *  Returns the unit price of the product.
     */
    private BigDecimal unitPrice;

}
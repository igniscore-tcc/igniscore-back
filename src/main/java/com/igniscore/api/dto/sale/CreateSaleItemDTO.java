package com.igniscore.api.dto.sale;

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
public class CreateSaleItemDTO {

    /**
     * Identifier of the product associated with the sale item.
     */
    private Integer productId;

    /**
     * Quantity of the product being sold.
     */
    private Integer quantity;

    /**
     * Unit price applied to the product at the time of sale.
     *
     * <p>{@link BigDecimal} is used to preserve
     * monetary precision and avoid floating-point inaccuracies.
     */
    private BigDecimal unitPrice;

    /**
     * Returns the product identifier.
     *
     * @return product ID
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * Returns the quantity of items.
     *
     * @return item quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Returns the unit price of the product.
     *
     * @return unit price
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Defines the product identifier.
     *
     * @param productId product ID
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * Defines the quantity of items.
     *
     * @param quantity item quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Defines the unit price of the product.
     *
     * @param unitPrice product unit price
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
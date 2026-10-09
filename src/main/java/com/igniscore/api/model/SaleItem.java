package com.igniscore.api.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Entity representing an individual item within a sale.
 *
 * <p>Stores the product, quantity, unit price, and total value associated
 * with a sale item. Quantity and unit price changes trigger automatic
 * recalculation of the total.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Represent products included in a sale</li>
 *     <li>Maintain quantity and pricing information</li>
 *     <li>Calculate the total item value</li>
 *     <li>Enforce quantity and unit price business rules</li>
 * </ul>
 *
 * <p>Persistence mapping:
 * <ul>
 *     <li>Mapped to table {@code sale_items}</li>
 *     <li>Associated with {@link Sale}</li>
 *     <li>Associated with {@link Product}</li>
 * </ul>
 */
@Getter
@Entity
@Table(name = "sale_items")
public class SaleItem {

    /**
     * Unique identifier of the sale item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_sale_item")
    private Integer id;

    /**
     * Quantity of the product included in the sale.
     */
    @Column(name = "quantity_sale_item", nullable = false)
    private Integer quantity;

    /**
     * Unit price applied to the product.
     *
     * <p>Uses fixed decimal precision to preserve monetary accuracy.
     */
    @Column(
            name = "unit_price_sale_item",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal unitPrice;

    /**
     * Total value of the item, calculated as quantity multiplied by unit price.
     */
    @Column(
            name = "total_sale_item",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal total;

    /**
     * Product associated with the sale item.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_prod", nullable = false)
    private Product product;

    /**
     * Sale associated with the item.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_sale", nullable = false)
    private Sale sale;

    /**
     * Protected constructor required by JPA.
     */
    protected SaleItem() {
    }

    /**
     * Creates a sale item and calculates its total value.
     *
     * @param product associated product
     * @param quantity product quantity
     * @param unitPrice product unit price
     * @throws IllegalArgumentException if quantity or unit price is invalid
     */
    public SaleItem(
            Product product,
            Integer quantity,
            BigDecimal unitPrice
    ) {
        validateQuantity(quantity);
        validateUnitPrice(unitPrice);

        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;

        recalculateTotal();
    }

    /**
     * Updates the item quantity and recalculates its total value.
     *
     * @param quantity new quantity
     * @throws IllegalArgumentException if quantity is null or not positive
     */
    public void changeQuantity(Integer quantity) {
        validateQuantity(quantity);

        this.quantity = quantity;
        recalculateTotal();
    }

    /**
     * Updates the unit price and recalculates the total item value.
     *
     * @param unitPrice new unit price
     * @throws IllegalArgumentException if the price is null or not positive
     */
    public void changeUnitPrice(BigDecimal unitPrice) {
        validateUnitPrice(unitPrice);

        this.unitPrice = unitPrice;
        recalculateTotal();
    }

    /**
     * Recalculates the total using the current quantity and unit price.
     */
    private void recalculateTotal() {
        this.total = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Validates the item quantity.
     *
     * @param quantity quantity to validate
     * @throws IllegalArgumentException if quantity is null or not positive
     */
    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    /**
     * Validates the unit price.
     *
     * @param unitPrice price to validate
     * @throws IllegalArgumentException if the price is null or not positive
     */
    private void validateUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null
                || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Unit price must be greater than zero"
            );
        }
    }

    /**
     * Associates this item with a sale.
     *
     * <p>Package-private visibility restricts relationship management
     * to operations within the model package.
     *
     * @param sale associated sale
     */
    void setSale(Sale sale) {
        this.sale = sale;
    }
}
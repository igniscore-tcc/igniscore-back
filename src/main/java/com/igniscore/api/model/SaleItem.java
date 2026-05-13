package com.igniscore.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Entity representing an individual item within a sale.
 *
 * <p>A {@link SaleItem} stores product information associated
 * with a specific sale, including quantity, unit price,
 * and calculated total value.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Represent products included in a sale</li>
 *     <li>Maintain item pricing information</li>
 *     <li>Calculate total item value</li>
 *     <li>Validate quantity and pricing rules</li>
 * </ul>
 *
 * <p>Persistence mapping:
 * <ul>
 *     <li>Mapped to table {@code sale_items}</li>
 *     <li>Associated with {@link Sale}</li>
 *     <li>Associated with {@link Product}</li>
 * </ul>
 */
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
     * <p>Uses fixed decimal precision to ensure
     * monetary accuracy.
     */
    @Column(
            name = "unit_price_sale_item",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal unitPrice;

    /**
     * Total value of the item.
     *
     * <p>Calculated using:
     * <pre>
     * quantity * unitPrice
     * </pre>
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
    protected SaleItem() {}

    /**
     * Creates a new sale item instance.
     *
     * <p>The constructor validates:
     * <ul>
     *     <li>Quantity must be greater than zero</li>
     *     <li>Unit price must be greater than zero</li>
     * </ul>
     *
     * <p>The total value is automatically calculated.
     *
     * @param product associated product
     * @param quantity product quantity
     * @param unitPrice product unit price
     *
     * @throws IllegalArgumentException when:
     * <ul>
     *     <li>Quantity is invalid</li>
     *     <li>Unit price is invalid</li>
     * </ul>
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
     * Updates the item quantity.
     *
     * <p>After updating the quantity,
     * the total value is recalculated.
     *
     * @param quantity new quantity
     */
    public void changeQuantity(Integer quantity) {

        validateQuantity(quantity);

        this.quantity = quantity;

        recalculateTotal();
    }

    /**
     * Updates the unit price.
     *
     * <p>After updating the price,
     * the total value is recalculated.
     *
     * @param unitPrice new unit price
     */
    public void changeUnitPrice(BigDecimal unitPrice) {

        validateUnitPrice(unitPrice);

        this.unitPrice = unitPrice;

        recalculateTotal();
    }

    /**
     * Recalculates the total item value.
     *
     * <p>Calculation formula:
     * <pre>
     * total = unitPrice * quantity
     * </pre>
     */
    private void recalculateTotal() {

        this.total = this.unitPrice.multiply(
                BigDecimal.valueOf(this.quantity)
        );
    }

    /**
     * Validates the item quantity.
     *
     * @param quantity quantity to validate
     *
     * @throws IllegalArgumentException if quantity
     * is null or less than or equal to zero
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
     *
     * @throws IllegalArgumentException if price
     * is null or less than or equal to zero
     */
    private void validateUnitPrice(BigDecimal unitPrice) {

        if (unitPrice == null ||
                unitPrice.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Unit price must be greater than zero"
            );
        }
    }

    /**
     * Returns the sale item identifier.
     *
     * @return sale item ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the product quantity.
     *
     * @return quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Returns the unit price.
     *
     * @return unit price
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Returns the total value.
     *
     * @return total amount
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Returns the associated product.
     *
     * @return product entity
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the associated sale.
     *
     * @return sale entity
     */
    public Sale getSale() {
        return sale;
    }

    /**
     * Defines the associated sale.
     *
     * <p>This method has package-private visibility
     * to restrict relationship management to
     * domain-level operations.
     *
     * @param sale associated sale
     */
    void setSale(Sale sale) {
        this.sale = sale;
    }
}
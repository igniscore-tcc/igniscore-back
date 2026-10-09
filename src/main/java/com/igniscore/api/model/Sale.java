
package com.igniscore.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a sale transaction within the system.
 *
 * <p>A sale aggregates one or more {@link SaleItem} instances, stores
 * payment information, tracks totals and discounts, and maintains
 * relationships with {@link Company} and {@link Client}.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Maintain sale financial state</li>
 *     <li>Manage the lifecycle of sale items</li>
 *     <li>Calculate totals and discounts</li>
 *     <li>Represent transactional sale data</li>
 * </ul>
 *
 * <p>Persistence mapping:
 * <ul>
 *     <li>Mapped to table {@code sales}</li>
 *     <li>Uses an auto-generated primary key</li>
 *     <li>Maintains a bidirectional relationship with {@link SaleItem}</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "sales")
public class Sale implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier of the sale.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_sale")
    private Integer id;

    /**
     * Sequential sale number used for identification.
     */
    @Column(name = "number_sale")
    private Integer numberSale;

    /**
     * Total quantity of items in the sale.
     */
    @Column(name = "quantity_items_sale", nullable = false)
    private Integer quantityItems = 0;

    /**
     * Discount applied to the sale.
     */
    @Column(
            name = "discount_sale",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * Final total amount of the sale after discounts.
     */
    @Column(
            name = "total_sale",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Date when the sale was created.
     */
    @Column(name = "date_sale", nullable = false)
    private LocalDate date;

    /**
     * Payment method used for the sale.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_sale", nullable = false, length = 30)
    private SaleType paymentMethod;

    /**
     * Current status of the sale.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_sale", nullable = false, length = 20)
    private SaleStatus status;

    /**
     * Type of document associated with the sale.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "document_sale")
    private SaleDocument type;

    /**
     * Document identification number.
     */
    @Column(name = "document_number", length = 10)
    private String document;

    /**
     * Due date associated with the sale payment.
     */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Timestamp when the sale was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the sale was deleted.
     *
     * <p>A null value indicates that the sale has not been deleted.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Company associated with the sale.
     *
     * <p>Used to enforce multi-tenant data isolation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_company", nullable = false)
    private Company company;

    /**
     * Client associated with the sale.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_client", nullable = false)
    private Client client;

    /**
     * Items belonging to the sale.
     *
     * <p>Item persistence and removal are cascaded from the sale entity.
     */
    @OneToMany(
            mappedBy = "sale",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<SaleItem> items = new ArrayList<>();

    /**
     * Protected constructor required by JPA.
     */
    protected Sale() {
    }

    /**
     * Creates a sale with its primary transaction details.
     *
     * @param date sale date
     * @param status initial sale status
     * @param type document type
     * @param document document identification number
     * @param dueDate payment due date
     * @param company company associated with the sale
     * @param client client associated with the sale
     * @param paymentMethod payment method
     */
    public Sale(
            LocalDate date,
            SaleStatus status,
            SaleDocument type,
            String document,
            LocalDate dueDate,
            Company company,
            Client client,
            SaleType paymentMethod
    ) {
        this.date = date;
        this.status = status;
        this.type = type;
        this.document = document;
        this.dueDate = dueDate;
        this.company = company;
        this.client = client;
        this.paymentMethod = paymentMethod;
    }

    public Sale(Sale sale) {
        this.id = sale.getId();
        this.numberSale = sale.getNumberSale();
        this.quantityItems = sale.getQuantityItems();
        this.discount = sale.getDiscount();
        this.total = sale.getTotal();
        this.date = sale.getDate();
        this.paymentMethod = sale.getPaymentMethod();
        this.status = sale.getStatus();
        this.type = sale.getType();
        this.document = sale.getDocument();
        this.dueDate = sale.getDueDate();
        this.createdAt = sale.getCreatedAt();
        this.deletedAt = sale.getDeletedAt();
        this.company = sale.getCompany();
        this.client = sale.getClient();
        this.items.addAll(sale.getItems());
    }

    /**
     * Adds an item to the sale and updates its quantity and total.
     *
     * @param item item to add
     */
    public void addItem(SaleItem item) {
        item.setSale(this);
        this.items.add(item);
        this.quantityItems += item.getQuantity();
        this.total = this.total.add(item.getTotal());
    }

    /**
     * Removes an item from the sale and updates its quantity and total.
     *
     * @param item item to remove
     */
    public void removeItem(SaleItem item) {
        if (this.items.remove(item)) {
            item.setSale(null);
            this.quantityItems -= item.getQuantity();
            this.total = this.total.subtract(item.getTotal());
        }
    }

    /**
     * Applies a discount to the sale.
     *
     * <p>The discount cannot be negative or exceed the total before
     * the current discount is applied.
     *
     * @param discount discount amount; null is treated as zero
     * @throws IllegalArgumentException if the discount is negative
     *                                  or exceeds the sale total
     */
    public void applyDiscount(BigDecimal discount) {
        if (discount == null) {
            discount = BigDecimal.ZERO;
        }

        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Discount cannot be negative"
            );
        }

        BigDecimal totalWithoutDiscount = this.total.add(this.discount);

        if (discount.compareTo(totalWithoutDiscount) > 0) {
            throw new IllegalArgumentException(
                    "Discount cannot exceed total"
            );
        }

        this.discount = discount;
        this.total = totalWithoutDiscount.subtract(discount);
    }
}
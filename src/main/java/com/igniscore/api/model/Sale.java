package com.igniscore.api.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity representing a sale transaction within the system.
 *
 * <p>A sale aggregates one or more {@link SaleItem} instances,
 * stores payment information, tracks totals and discounts,
 * and maintains relationships with both {@link Company}
 * and {@link Client}.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Maintain sale financial state</li>
 *     <li>Manage sale items lifecycle</li>
 *     <li>Calculate totals and discounts</li>
 *     <li>Represent transactional sale data</li>
 * </ul>
 *
 * <p>Persistence mapping:
 * <ul>
 *     <li>Mapped to table {@code sales}</li>
 *     <li>Uses auto-generated primary key</li>
 *     <li>Contains bidirectional relationship with {@link SaleItem}</li>
 * </ul>
 */
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

    @Column(name = "number_sale")
    private Integer numberSale;

    /**
     * Total quantity of items in the sale.
     *
     * <p>This value is automatically updated
     * when items are added or removed.
     */
    @Column(name = "quantity_items_sale", nullable = false)
    private Integer quantityItems = 0;

    /**
     * Discount applied to the sale.
     *
     * <p>Stored using fixed decimal precision
     * for financial consistency.
     */
    @Column(
            name = "discount_sale",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * Final total amount of the sale.
     *
     * <p>This value is recalculated whenever
     * items are added, removed, or discounts applied.
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
    private PaymentMethod paymentMethod;

    /**
     * Current status of the sale.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_sale", nullable = false, length = 20)
    private SaleStatus status;

    /**
     * Due date associated with the sale payment.
     */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Company associated with the sale.
     *
     * <p>Used for multi-tenant data isolation.
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
     * Collection of items belonging to the sale.
     *
     * <p>All item lifecycle operations are cascaded
     * from the parent sale entity.
     */
    @OneToMany(
            mappedBy = "sale",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<SaleItem> items = new ArrayList<>();

    /**
     * Adds a new item to the sale.
     *
     * <p>This method:
     * <ul>
     *     <li>Associates the item with the sale</li>
     *     <li>Updates total item quantity</li>
     *     <li>Recalculates sale total</li>
     * </ul>
     *
     * @param item item to be added
     */
    public void addItem(SaleItem item) {

        item.setSale(this);

        this.items.add(item);

        this.quantityItems += item.getQuantity();

        this.total = this.total.add(item.getTotal());
    }

    /**
     * Removes an item from the sale.
     *
     * <p>This method:
     * <ul>
     *     <li>Removes item association</li>
     *     <li>Updates item quantity</li>
     *     <li>Recalculates total value</li>
     * </ul>
     *
     * @param item item to be removed
     */
    public void removeItem(SaleItem item) {

        this.items.remove(item);

        item.setSale(null);

        this.quantityItems -= item.getQuantity();

        this.total = this.total.subtract(item.getTotal());
    }

    /**
     * Applies a discount to the sale.
     *
     * <p>Validation rules:
     * <ul>
     *     <li>Discount cannot be negative</li>
     *     <li>Discount cannot exceed sale total</li>
     * </ul>
     *
     * <p>The total value is recalculated after applying
     * the new discount amount.
     *
     * @param discount discount value
     *
     * @throws IllegalArgumentException when:
     * <ul>
     *     <li>Discount is negative</li>
     *     <li>Discount exceeds total amount</li>
     * </ul>
     */
    public void applyDiscount(BigDecimal discount) {

        if (discount == null) {
            discount = BigDecimal.ZERO;
        }

        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }

        BigDecimal totalWithoutDiscount = this.total.add(this.discount);

        if (discount.compareTo(totalWithoutDiscount) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed total");
        }

        this.discount = discount;
        this.total = totalWithoutDiscount.subtract(discount);
    }

    public Sale() {
    }

    public Sale(LocalDate date, SaleStatus status, LocalDate dueDate, Company company, Client client, PaymentMethod paymentMethod) {
        this.date = date;
        this.status = status;
        this.dueDate = dueDate;
        this.company = company;
        this.client = client;
        this.paymentMethod = paymentMethod;
    }

    /**
     * Returns the sale identifier.
     *
     * @return sale ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the total quantity of items.
     *
     * @return total item quantity
     */
    public Integer getQuantityItems() {
        return quantityItems;
    }

    /**
     * Returns the applied discount.
     *
     * @return discount value
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Returns the total amount of the sale.
     *
     * @return sale total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Returns the sale date.
     *
     * @return sale date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the payment method.
     *
     * @return payment method
     */
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Returns the current sale status.
     *
     * @return sale status
     */
    public SaleStatus getStatus() {
        return status;
    }

    /**
     * Returns the payment due date.
     *
     * @return due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Returns the associated company.
     *
     * @return company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Returns the associated client.
     *
     * @return client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Returns an immutable list of sale items.
     *
     * <p>Prevents external modification of
     * the internal collection.
     *
     * @return immutable list of items
     */
    public List<SaleItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Integer getNumberSale() {
        return numberSale;
    }

    /**
     * Defines the sale identifier.
     *
     * @param id sale ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Defines the sale date.
     *
     * @param date sale date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Defines the payment method.
     *
     * @param paymentMethod payment method
     */
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Defines the sale status.
     *
     * @param status sale status
     */
    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    /**
     * Defines the due date.
     *
     * @param dueDate payment due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Defines the associated company.
     *
     * @param company company entity
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * Defines the associated client.
     *
     * @param client client entity
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public void setNumberSale(Integer numberSale) {
        this.numberSale = numberSale;
    }
}
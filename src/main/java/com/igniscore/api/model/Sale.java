package com.igniscore.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_sale")
    private Integer id;

    @Column(name = "quantity_items_sale", nullable = false)
    private Integer quantity_items;

    @Column(
            name = "discount_sale",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal discount;

    @Column(
            name = "total_sale",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal total;

    @Column(name = "date_sale", nullable = false)
    private LocalDate date;

    @Column(name = "type_sale", nullable = false, length = 30)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_sale", nullable = false, length = 20)
    private SaleStatus status;

    @Column(name = "due_date", nullable = false)
    private LocalDate due_date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_company", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_client", nullable = false)
    private Client client;

    public Integer getId() {
        return id;
    }

    public Integer getQuantityItems() {
        return quantity_items;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return due_date;
    }

    public Company getCompany() {
        return company;
    }

    public Client getClient() {
        return client;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setQuantityItems(Integer quantity_items) {
        this.quantity_items = quantity_items;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public void setDue_date(LocalDate due_date) {
        this.due_date = due_date;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

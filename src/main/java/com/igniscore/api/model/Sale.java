package com.igniscore.api.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_sale")
    private Integer id;

    @Column(name = "quantity_items_sale")
    private Integer quantity_items;

    @Column(name = "discount_sale")
    private Float discount;

    @Column(name = "total_sale")
    private Float total;

    @Column(name = "date_sale")
    private LocalDate date;

    @Column(name = "type_sale")
    private String type;

    @Column(name = "status_sale")
    private SaleStatus status;

    @Column(name = "due_date")
    private LocalDate due_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_client")
    private Client client;

    public Integer getId() {
        return id;
    }

    public Integer getQuantity_items() {
        return quantity_items;
    }

    public Float getDiscount() {
        return discount;
    }

    public Float getTotal() {
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

    public LocalDate getDue_date() {
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

    public void setQuantity_items(Integer quantity_items) {
        this.quantity_items = quantity_items;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public void setTotal(Float total) {
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

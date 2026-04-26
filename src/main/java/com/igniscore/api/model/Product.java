package com.igniscore.api.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Entity representing a product within the system.
 *
 * <p>This class models a product that belongs to a {@link Company},
 * supporting a multi-tenant architecture where each company manages
 * its own product catalog.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Persist product-related data</li>
 *     <li>Represent business attributes such as name, type, price, and validity</li>
 *     <li>Maintain association with a company</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Uses {@link LocalDate} for validity to represent date-only values</li>
 *     <li>Company relationship is lazily loaded for performance optimization</li>
 * </ul>
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * Primary key identifier.
     */
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_prod")
    private Integer id;

    /**
     * Product name.
     */
    @Column(name = "name_prod")
    private String name;

    /**
     * Product type or category.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_prod")
    private ProductType type;

    /**
     * Product validity or expiration date.
     */
    @Column(name = "validity_prod")
    private LocalDate validity;

    /**
     * Product batch or lot identifier.
     */
    @Column(name = "lot_prod")
    private String lot;

    /**
     * Product price.
     *
     * <p>Note: Currently uses Float, which may lead to precision issues.
     */
    @Column(name = "price_prod")
    private Float price;


    @Column(name = "status_prod")
    private Boolean status;

    /**
     * Associated company (multi-tenant relationship).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company")
    private Company company;

    // --- Getters ---

    public Integer getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public Float getPrice() {
        return price;
    }

    public String getLot() {
        return lot;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public ProductType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Boolean getStatus() {
        return status;
    }

    // --- Setters ---

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
package com.igniscore.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igniscore.api.dto.product.ProductStoreDTO;
import com.igniscore.api.dto.product.ProductUpdateDTO;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity representing a product managed within the platform.
 *
 * <p>This entity models products owned by a specific {@link Company},
 * supporting multi-tenant data isolation where each company maintains
 * an independent product catalog.
 *
 * <p>The entity stores operational and commercial product information,
 * including identification data, categorization, pricing, validity,
 * batch tracking, and activation status.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Persist product domain data</li>
 *     <li>Represent product lifecycle state</li>
 *     <li>Maintain company ownership association</li>
 *     <li>Support logical deletion through status flag</li>
 * </ul>
 *
 * <p>Persistence notes:
 * <ul>
 *     <li>Mapped to table {@code products}</li>
 *     <li>Uses {@link GenerationType#IDENTITY} for primary key generation</li>
 *     <li>Company association is lazily loaded for performance optimization</li>
 *     <li>Price uses {@link BigDecimal} with fixed precision and scale</li>
 * </ul>
 *
 * <p>Serialization notes:
 * <ul>
 *     <li>Hibernate lazy-loading proxy properties are ignored during JSON serialization</li>
 *     <li>Company association is excluded from JSON output to avoid unnecessary exposure
 *     and serialization recursion issues</li>
 * </ul>
 */
@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
@Entity
@Table(name = "products")
public class Product implements Serializable {

    /**
     * Serialization version identifier.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key identifier of the product.
     */
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_prod")
    private Integer id;

    /**
     * Commercial or display name of the product.
     */
    @Column(name = "name_prod")
    private String name;

    /**
     * Product classification or category.
     *
     * <p>Persisted using the enum constant name.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_prod")
    private ProductType type;

    /**
     * Product expiration or validity date.
     *
     * <p>Uses {@link LocalDate} because time-zone or time-of-day
     * precision is not required for this domain attribute.
     */
    @Column(name = "validity_prod")
    private LocalDate validity;

    /**
     * Batch or lot identifier used for traceability.
     */
    @Column(name = "lot_prod")
    private String lot;

    /**
     * Monetary value of the product.
     *
     * <p>Stored using fixed decimal precision:
     * <ul>
     *     <li>Precision: 10</li>
     *     <li>Scale: 2</li>
     * </ul>
     *
     * <p>This configuration supports values up to 99,999,999.99.
     */
    @Column(name = "price_prod", precision = 10, scale = 2)
    private BigDecimal price;


    /**
     * Logical activation status of the product.
     *
     * <p>Used to implement soft deletion semantics:
     * <ul>
     *     <li>{@code true}  = active product</li>
     *     <li>{@code false} = logically deleted/inactive product</li>
     * </ul>
     */
    @Column(name = "status_prod")
    private Boolean status;

    /**
     * Company that owns the product.
     *
     * <p>This association enforces multi-tenant ownership boundaries.
     *
     * <p>Configured with lazy loading to reduce unnecessary entity loading
     * during standard product retrieval operations.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company")
    @JsonIgnore
    private Company company;

    public Product() {
    }

    public Product(ProductStoreDTO dto, Company company) {
        this.name = dto.getName();
        this.type = dto.getType();
        this.validity = dto.getValidity();
        this.lot = dto.getLot();
        this.price = dto.getPrice();
        this.company = company;
        this.status = true;
    }

    public Product(Product product) {
        this.id = product.id;
        this.name = product.name;
        this.type = product.type;
        this.validity = product.validity;
        this.lot = product.lot;
        this.price = product.price;
        this.status = product.status;
    }

    // --- Getters ---

    public Integer getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public BigDecimal getPrice() {
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

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void update(ProductUpdateDTO dto) {

        if (dto.getName() != null) {
            this.name = dto.getName();
        }

        if (dto.getType() != null) {
            this.type = dto.getType();
        }

        if (dto.getValidity() != null) {
            this.validity = dto.getValidity();
        }

        if (dto.getLot() != null) {
            this.lot = dto.getLot();
        }

        if (dto.getPrice() != null) {
            this.price = dto.getPrice();
        }
    }

    public void deactivate() {
        this.status = false;
    }
}
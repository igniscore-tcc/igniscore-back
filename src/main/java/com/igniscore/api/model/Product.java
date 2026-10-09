package com.igniscore.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igniscore.api.dto.product.ProductStoreDTO;
import com.igniscore.api.dto.product.ProductUpdateDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity representing a product managed within the platform.
 *
 * <p>Each product belongs to a specific company, supporting multi-tenant
 * data isolation and independent product catalogs.
 *
 * <p>The entity stores product identification, classification, validity,
 * batch tracking, pricing, and activation status.
 *
 * <p>Products support logical deactivation through the status field,
 * preserving historical records in the database.
 *
 * <p>Persistence details:
 * <ul>
 *     <li>Mapped to the {@code products} table</li>
 *     <li>Uses identity-based primary key generation</li>
 *     <li>Loads the company association lazily</li>
 *     <li>Stores monetary values with precision 10 and scale 2</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_prod")
    private Integer id;

    /**
     * Sequential product number used for identification.
     */
    @Column(name = "number_product")
    private Integer numberProduct;

    /**
     * Commercial or display name of the product.
     */
    @Column(name = "name_prod")
    private String name;

    /**
     * Product classification, persisted as an enum constant name.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type_prod")
    private ProductType type;

    /**
     * Product expiration or validity date.
     */
    @Column(name = "validity_prod")
    private LocalDate validity;

    /**
     * Batch or lot identifier used for product traceability.
     */
    @Column(name = "lot_prod")
    private String lot;

    /**
     * Monetary value of the product, stored with precision 10 and scale 2.
     */
    @Column(name = "price_prod", precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Indicates whether the product is active.
     *
     * <p>{@code true} represents an active product; {@code false}
     * represents a deactivated product.
     */
    @Column(name = "status_prod")
    private Boolean status;

    /**
     * Company that owns the product.
     *
     * <p>The association is lazily loaded and excluded from JSON
     * serialization to avoid exposing the entity relationship directly.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company")
    @JsonIgnore
    private Company company;

    /**
     * Creates a product from registration data and associates it with
     * its owning company.
     *
     * @param dto product registration data
     * @param company company that owns the product
     */
    public Product(ProductStoreDTO dto, Company company) {
        this.name = dto.getName();
        this.type = dto.getType();
        this.validity = dto.getValidity();
        this.lot = dto.getLot();
        this.price = dto.getPrice();
        this.company = company;
        this.status = true;
    }

    /**
     * Creates a copy of an existing product.
     *
     * <p>Copies the product's scalar fields without copying its company
     * association. Useful for preserving a snapshot before modification.
     *
     * @param product product whose data will be copied
     */
    public Product(Product product) {
        this.id = product.id;
        this.numberProduct = product.numberProduct;
        this.name = product.name;
        this.type = product.type;
        this.validity = product.validity;
        this.lot = product.lot;
        this.price = product.price;
        this.status = product.status;
        this.company = product.getCompany();
    }

    /**
     * Applies non-null values from an update DTO to the current product.
     *
     * <p>Fields omitted from the request remain unchanged.
     *
     * @param dto product update data
     */
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

    /**
     * Deactivates the product without physically deleting its database record.
     */
    public void deactivate() {
        this.status = false;
    }
}
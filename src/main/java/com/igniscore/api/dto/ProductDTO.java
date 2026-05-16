package com.igniscore.api.dto;

import com.igniscore.api.model.ProductType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing product data exposed by the API.
 *
 * <p>This DTO is intended for outbound data transport, primarily in API
 * response payloads. It provides a stable external representation of
 * product information while decoupling consumers from the internal
 * persistence model.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Expose product-related data to external consumers</li>
 *     <li>Prevent direct exposure of JPA entities</li>
 *     <li>Provide a serialization-friendly structure for API responses</li>
 *     <li>Represent company ownership information through nested DTOs</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Uses {@link LocalDate} for date-only domain values</li>
 *     <li>Uses {@link BigDecimal} for monetary representation</li>
 *     <li>Contains a nested {@link CompanyDTO} instead of exposing the entity directly</li>
 *     <li>Primarily intended for read operations</li>
 * </ul>
 */
public class ProductDTO {

    /**
     * Unique identifier of the product.
     */
    private Integer id;

    /**
     * Commercial or display name of the product.
     */
    private String name;

    /**
     * Product classification or category.
     */
    private ProductType type;
    /**
     * Product expiration or validity date.
     *
     * <p>Represents a date-only value without time-zone information.
     */
    private LocalDate validity;
    /**
     * Product batch or lot identifier used for traceability.
     */
    private String lot;
    /**
     * Monetary value associated with the product.
     *
     * <p>Uses {@link BigDecimal} to preserve decimal precision
     * required for financial calculations.
     */
    private BigDecimal price;
    /**
     * Company associated with the product ownership.
     *
     * <p>Represented through a DTO abstraction to avoid exposing
     * persistence entities directly to API consumers.
     */
    private CompanyDTO company;

    // --- Getters ---

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public String getLot() {
        return lot;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public CompanyDTO getCompany() {
        return company;
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

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }
}
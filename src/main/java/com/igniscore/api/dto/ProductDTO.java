package com.igniscore.api.dto;

import com.igniscore.api.model.ProductType;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a product.
 *
 * <p>This class is used to expose product data outside the service layer,
 * typically in API responses. It decouples the internal {@code Product} entity
 * from external consumers.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Provide a safe representation of product data</li>
 *     <li>Hide internal entity structure</li>
 *     <li>Aggregate related data (e.g., company information)</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Includes a nested {@link CompanyDTO} to represent ownership</li>
 *     <li>Uses {@link LocalDate} for date-only values</li>
 *     <li>Intended for read operations (API responses)</li>
 * </ul>
 */
public class ProductDTO {

    /**
     * Product identifier.
     */
    private Integer id;

    /**
     * Product name.
     */
    private String name;

    /**
     * Product type or category.
     */
    private ProductType type;

    /**
     * Product validity or expiration date.
     */
    private LocalDate validity;

    /**
     * Product batch or lot identifier.
     */
    private String lot;

    /**
     * Product price.
     *
     * <p>Note: Uses Float, which may lead to precision issues.
     */
    private Float price;

    /**
     * Company associated with the product.
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

    public Float getPrice() {
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

    public void setPrice(Float price) {
        this.price = price;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }
}
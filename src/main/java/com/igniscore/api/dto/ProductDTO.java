package com.igniscore.api.dto;

import java.time.LocalDate;

public class ProductDTO {
    private Integer id;
    private String name;
    private String type;
    private LocalDate validity;
    private String lot;
    private Float price;
    private CompanyDTO company;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
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

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
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

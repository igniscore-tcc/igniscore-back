package com.igniscore.api.dto.address;

import java.io.Serial;
import java.io.Serializable;

public class AddressUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer clientId;
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
    private String cep;

    public AddressUpdateDTO() {
    }

    public Integer getClientId() {
        return clientId;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCep() {
        return cep;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
package com.igniscore.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igniscore.api.dto.address.AddressRegisterDTO;
import com.igniscore.api.dto.address.AddressUpdateDTO;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler"
})
@Entity
@Table(name = "addresses")
public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_address")
    private Integer id;

    @Column(name = "street_address", nullable = false, length = 150)
    private String street;

    @Column(name = "number_address", nullable = false, length = 20)
    private String number;

    @Column(name = "city_address", nullable = false, length = 100)
    private String city;

    @Column(name = "neighborhood_address")
    private String neighborhood;

    @Column(name = "state_address", nullable = false, length = 2)
    private String state;

    @Column(name = "cep_address", nullable = false, length = 8)
    private String cep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "fk_id_client",
            nullable = false
    )
    @JsonIgnore
    private Client client;

    public Address() {
    }

    public Address(AddressRegisterDTO dto, Client client) {
        this.street = dto.getStreet();
        this.number = dto.getNumber();
        this.city = dto.getCity();
        this.neighborhood = dto.getNeighborhood();
        this.state = dto.getState();
        this.cep = dto.getCep();
        this.client = client;
    }

    public Address(Address address) {
        this.id = address.id;
        this.street = address.street;
        this.number = address.number;
        this.neighborhood = address.neighborhood;
        this.city = address.city;
        this.state = address.state;
        this.cep = address.cep;
        this.client = address.client;
    }

    public Address(AddressUpdateDTO dto, Client client) {
        this.street = dto.getStreet();
        this.number = dto.getNumber();
        this.city = dto.getCity();
        this.neighborhood = dto.getNeighborhood();
        this.state = dto.getState();
        this.cep = dto.getCep();
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public String getCep() {
        return cep;
    }

    public String getState() {
        return state;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public Integer getId() {
        return id;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void update(AddressUpdateDTO dto) {
        if (dto.getStreet() != null) this.street = dto.getStreet();
        if (dto.getNumber() != null) this.number = dto.getNumber();
        if (dto.getNeighborhood() != null) this.neighborhood = dto.getNeighborhood();
        if (dto.getCity() != null) this.city = dto.getCity();
        if (dto.getState() != null) this.state = dto.getState();
        if (dto.getCep() != null) this.cep = dto.getCep();
    }
}

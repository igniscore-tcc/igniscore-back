package com.igniscore.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.igniscore.api.dto.address.AddressRegisterDTO;
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
        this.state = dto.getState();
        this.cep = dto.getCep();
        this.client = client;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

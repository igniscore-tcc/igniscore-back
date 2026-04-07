package com.igniscore.api.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_client")
    private Integer id;

    @Column(name = "name_client", nullable = false)
    private String name;

    @Column(name = "cnpj_client", nullable = false, unique = true)
    private String cnpj;

    @Column(name = "email_client", nullable = false, unique = true)
    private String email;

    @Column(name = "number_client", nullable = false, unique = true)
    private Integer number;

    @Column(name = "ie_client", nullable = false, unique = true)
    private String ie;

    @Column(name = "ufIe_client", nullable = false, unique = true)
    private String ufIe;

    @Column(name = "obs_client", nullable = false, unique = true)
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company")
    private Company company;

    public Integer getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEmail() {
        return email;
    }

    public Integer getNumber() {
        return number;
    }

    public String getIe() {
        return ie;
    }

    public String ufIe() {
        return ufIe;
    }

    public String getObs() {
        return obs;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public void setUfIe(String ufIe) {this.ufIe = ufIe;}

    public void setObs(String obs) {this.obs = obs;}

    public void setCompany(Company company) {
        this.company = company;
    }

}


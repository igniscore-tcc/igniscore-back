package com.igniscore.api.model;

import jakarta.persistence.*;

//import java.time.LocalDate;

@Entity
@Table(
        name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_client_per_company", columnNames = {"fk_id_company", "number_client"})
        }
)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_client")
    private Integer id;

    @Column(name = "name_client", nullable = false)
    private String name;

    @Column(name = "cnpj_client", nullable = false)
    private String cnpj;

    @Column(name = "email_client", nullable = false)
    private String email;

    @Column(name = "phone_client")
    private String phone;

    @Column(name = "number_client", nullable = false, insertable = false, updatable = false)
    private Integer number;

    @Column(name = "ie_client")
    private String ie;

    @Column(name = "uf_ie_client")
    private String uf_ie;

    @Column(name = "obs_client")
    private String obs;

    @Column(name = "cpf_client")
    private Integer cpf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company", nullable = false)
    private Company company;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Integer getNumber() {
        return number;
    }

    public String getIe() {
        return ie;
    }

    public String getUfIe() {
        return uf_ie;
    }

    public String getObs() {
        return obs;
    }

    public Integer getCpf() {
        return cpf;
    }

    public Company getCompany() {
        return company;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public void setUfIe(String uf_ie) {
        this.uf_ie = uf_ie;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public void setCpf(Integer cpf) {
        this.cpf = cpf;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
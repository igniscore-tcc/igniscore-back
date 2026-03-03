package com.igniscore.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_company")
    private Integer id;

    @Column(name = "name_company", nullable = false, length = 150)
    private String name;

    @Column(name = "cnpj_company", nullable = false, length = 18)
    private String cnpj;

    @Column(name = "ie_company", length = 14)
    private String ie;

    @Column(name = "uf_ie_company", length = 2)
    private String ufIe;

    @Column(name = "email_company", nullable = false, length = 254)
    private String email;

    @Column(name = "phone_company", nullable = false, length = 13)
    private String phone;
}

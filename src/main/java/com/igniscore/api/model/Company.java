package com.igniscore.api.model;

import jakarta.persistence.*;

/**
 * Entity representing a company within the system.
 *
 * <p>This class models a business entity that owns resources such as
 * users, clients, and products in a multi-tenant architecture.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Store company identification and contact information</li>
 *     <li>Act as a tenant boundary for data isolation</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>CNPJ is used as a unique business identifier (Brazil-specific)</li>
 *     <li>Email and phone are also unique to avoid duplication</li>
 *     <li>IE (state registration) may be optional depending on jurisdiction</li>
 * </ul>
 */
@Entity
@Table(name = "companies")
public class Company {

    /**
     * Primary key identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_company")
    private Integer id;

    /**
     * Company legal or trade name.
     */
    @Column(name = "name_company", nullable = false)
    private String name;

    /**
     * Company CNPJ (Brazilian business identifier).
     * <p>Must be unique across the system.
     */
    @Column(name = "cnpj_company", nullable = false, unique = true)
    private String cnpj;

    /**
     * State registration number (Inscrição Estadual).
     * <p>May be null depending on company type.
     */
    @Column(name = "ie_company", unique = true)
    private String ie;

    /**
     * State abbreviation associated with IE (e.g., SP, RJ).
     */
    @Column(name = "uf_ie_company", length = 2)
    private String ufIe;

    /**
     * Company contact email.
     * <p>Must be unique across the system.
     */
    @Column(name = "email_company", nullable = false, unique = true)
    private String email;

    /**
     * Company contact phone number.
     * <p>Must be unique across the system.
     */
    @Column(name = "phone_company", nullable = false, unique = true)
    private String phone;

    // --- Getters ---

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getIe() {
        return ie;
    }

    public String getUfIe() {
        return ufIe;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    // --- Setters ---

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public void setUfIe(String ufIe) {
        this.ufIe = ufIe;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
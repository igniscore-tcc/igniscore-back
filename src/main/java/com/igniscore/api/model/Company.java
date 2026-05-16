package com.igniscore.api.model;

import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * JPA entity representing a company registered within the platform.
 *
 * <p>This entity acts as the primary tenant boundary in the system's
 * multi-tenant architecture. All business resources such as users,
 * products, clients, and operational records are associated with
 * a specific company instance.
 *
 * <p>Main responsibilities:
 * <ul>
 *     <li>Persist company identification data</li>
 *     <li>Store business contact information</li>
 *     <li>Provide tenant-level ownership and isolation</li>
 *     <li>Support regulatory and fiscal identification requirements</li>
 * </ul>
 *
 * <p>Persistence notes:
 * <ul>
 *     <li>Mapped to table {@code companies}</li>
 *     <li>Uses {@link GenerationType#IDENTITY} for primary key generation</li>
 *     <li>CNPJ, email, and phone fields are globally unique</li>
 *     <li>State registration (IE) may be nullable depending on company classification</li>
 * </ul>
 *
 * <p>Domain notes:
 * <ul>
 *     <li>CNPJ represents the Brazilian federal business identifier</li>
 *     <li>IE (Inscrição Estadual) represents a state-level tax registration</li>
 *     <li>UF stores the federative unit associated with the IE registration</li>
 * </ul>
 */
@Entity
@Table(name = "companies")
public class Company implements Serializable {
    /**
     * Serialization version identifier.
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Primary key identifier of the company.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_company")
    private Integer id;
    /**
     * Legal or trade name of the company.
     *
     * <p>This field is mandatory.
     */
    @Column(name = "name_company", nullable = false)
    private String name;
    /**
     * Brazilian federal business identifier (CNPJ).
     *
     * <p>This field must be unique across the system.
     *
     * <p>Used as the primary fiscal and legal identification
     * of the company.
     */
    @Column(name = "cnpj_company", nullable = false, unique = true)
    private String cnpj;
    /**
     * State tax registration number (Inscrição Estadual - IE).
     *
     * <p>This field may be optional depending on the company's
     * taxation regime and business activity.
     *
     * <p>Must be unique when provided.
     */
    @Column(name = "ie_company", unique = true)
    private String ie;
    /**
     * Federative unit (UF) associated with the state registration.
     *
     * <p>Examples:
     * <ul>
     *     <li>SP - São Paulo</li>
     *     <li>RJ - Rio de Janeiro</li>
     *     <li>MG - Minas Gerais</li>
     * </ul>
     */
    @Column(name = "uf_ie_company", length = 2)
    private String ufIe;
    /**
     * Primary contact email address of the company.
     *
     * <p>This field is mandatory and must be unique across the system.
     */
    @Column(name = "email_company", nullable = false, unique = true)
    private String email;
    /**
     * Primary contact phone number of the company.
     *
     * <p>This field is mandatory and must be unique across the system.
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
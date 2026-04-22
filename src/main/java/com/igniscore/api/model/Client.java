package com.igniscore.api.model;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

/**
 * Entity representing a client within the system.
 *
 * <p>This class models a client associated with a specific {@link Company},
 * supporting a multi-tenant architecture where each company manages its own clients.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Store client identification and contact information</li>
 *     <li>Maintain association with a company (tenant isolation)</li>
 *     <li>Support unique client numbering within a company</li>
 * </ul>
 *
 * <p>Database constraints:
 * <ul>
 *     <li>Each client has a unique "number" within its company</li>
 *     <li>Enforced via composite unique constraint (company + number)</li>
 * </ul>
 */
@Entity
@Table(
        name = "clients",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_client_per_company",
                        columnNames = {"fk_id_company", "number_client"}
                )
        }
)
public class Client {

    /**
     * Primary key identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_client")
    private Integer id;

    /**
     * Client name or legal entity name.
     */
    @Column(name = "name_client", nullable = false)
    private String name;

    /**
     * Client CNPJ (Brazilian business identifier).
     */
    @Column(name = "cnpj_client", nullable = false)
    private String cnpj;

    /**
     * Client contact email.
     */
    @Column(name = "email_client", nullable = false)
    private String email;

    /**
     * Client phone number.
     */
    @Column(name = "phone_client")
    private String phone;

    /**
     * Sequential client number within a company.
     *
     * <p>This field is managed at the database level
     * (not insertable or updatable from application code).
     */
    @Column(name = "number_client", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private Integer number;

    /**
     * State registration (Inscrição Estadual).
     */
    @Column(name = "ie_client")
    private String ie;

    /**
     * State abbreviation associated with IE.
     */
    @Column(name = "uf_ie_client")
    private String uf_ie;

    /**
     * Additional notes or observations about the client.
     */
    @Column(name = "obs_client")
    private String obs;

    /**
     * Client CPF (for individuals).
     */
    @Column(name = "cpf_client")
    private String cpf;

    /**
     * Associated company (multi-tenant relationship).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_company", nullable = false)
    private Company company;

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

    public String getCpf() {
        return cpf;
    }

    public Company getCompany() {
        return company;
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

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
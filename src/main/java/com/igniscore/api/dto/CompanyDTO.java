package com.igniscore.api.dto;

/**
 * Data Transfer Object (DTO) representing a company.
 *
 * <p>This class is used to expose company data outside the service layer,
 * typically in API responses. It decouples the internal {@code Company}
 * entity from external consumers.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Provide a safe and structured representation of company data</li>
 *     <li>Prevent direct exposure of persistence entities</li>
 *     <li>Serve as a contract between backend and API clients</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *     <li>Contains only relevant fields for external use</li>
 *     <li>Mutable structure (getters/setters) allows flexible mapping</li>
 *     <li>Can be adapted for different response shapes if needed</li>
 * </ul>
 */
public class CompanyDTO {

    /**
     * Company identifier.
     */
    private Integer id;

    /**
     * Company name.
     */
    private String name;

    /**
     * Company contact email.
     */
    private String email;

    /**
     * Company CNPJ (Brazilian business identifier).
     */
    private String cnpj;

    /**
     * State registration (Inscrição Estadual).
     */
    private String ie;

    /**
     * State abbreviation associated with IE.
     */
    private String ufIe;

    /**
     * Company contact phone number.
     */
    private String phone;

    // --- Getters ---

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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

    public void setEmail(String email) {
        this.email = email;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
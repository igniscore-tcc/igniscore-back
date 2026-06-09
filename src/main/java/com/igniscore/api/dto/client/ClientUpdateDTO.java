package com.igniscore.api.dto.client;

import com.igniscore.api.validation.ValidCNPJ;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object (DTO) used for client update operations.
 *
 * <p>Represents a mutable payload where fields are optional and may be
 * partially provided. Validation is applied only to fields that are present,
 * allowing flexible update semantics.
 *
 * <p><strong>Validation strategy:</strong>
 * <ul>
 *     <li>Pattern-based validation for formatted fields (CPF, CNPJ, phone)</li>
 *     <li>Cross-field validation to enforce identification requirements</li>
 * </ul>
 *
 * <p><strong>Domain constraints:</strong>
 * <ul>
 *     <li>At least one of CPF or CNPJ must be provided</li>
 *     <li>CPF and CNPJ accept both numeric-only and formatted inputs</li>
 *     <li>Phone must contain 10 or 11 digits if provided</li>
 * </ul>
 *
 * <p><strong>Update semantics:</strong>
 * <ul>
 *     <li>All fields are optional except for business rules enforced via validation</li>
 *     <li>Null fields may indicate no change, depending on service-layer handling</li>
 *     <li>The {@code id} field identifies the target entity to update</li>
 * </ul>
 *
 * <p><strong>Notes:</strong>
 * <ul>
 *     <li>This DTO is intended for write operations only</li>
 *     <li>Does not contain business logic beyond validation constraints</li>
 * </ul>
 */
public class ClientUpdateDTO {

    /**
     * Identifier of the client to be updated.
     */
    private Integer id;

    /**
     * Updated client name.
     */
    private String name;

    /**
     * Updated client email address.
     */
    private String email;

    /**
     * Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica).
     *
     * <p>Accepts either:
     * <ul>
     *     <li>14 numeric digits</li>
     * </ul>
     */
    @ValidCNPJ
    private String cnpj;

    /**
     * Brazilian CPF (Cadastro de Pessoas Físicas).
     *
     * <p>Accepts either:
     * <ul>
     *     <li>11 numeric digits</li>
     *     <li>Formatted pattern: XXX.XXX.XXX-XX</li>
     * </ul>
     */
    @Pattern(
            regexp = "(\\d{11})|(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})",
            message = "Invalid CPF format"
    )
    private String cpf;

    /**
     * Client phone number.
     *
     * <p>Must contain 10 or 11 digits if provided.
     */
    @Pattern(
            regexp = "\\d{10,11}",
            message = "Phone must contain 10 or 11 digits"
    )
    private String phone;

    /**
     * State registration (Inscrição Estadual).
     */
    private String ie;

    /**
     * Federative unit (state) associated with the state registration.
     */
    private String ufIe;

    /**
     * Additional notes or observations about the client.
     */
    private String obs;

    public ClientUpdateDTO() {
    }

    public ClientUpdateDTO(Integer id, String name, String email, String cnpj, String cpf, String phone, String ie, String ufIe, String obs) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cnpj = cnpj;
        this.cpf = cpf;
        this.phone = phone;
        this.ie = ie;
        this.ufIe = ufIe;
        this.obs = obs;
    }

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

    public String getCpf() {
        return cpf;
    }

    public String getPhone() {
        return phone;
    }

    public String getIe() {
        return ie;
    }

    public String getUfIe() {
        return ufIe;
    }

    public String getObs() {
        return obs;
    }

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

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public void setUfIe(String ufIe) {
        this.ufIe = ufIe;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
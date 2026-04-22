package com.igniscore.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;

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
     *     <li>Formatted pattern: XX.XXX.XXX/XXXX-XX</li>
     * </ul>
     */
    @Pattern(
            regexp = "(\\d{14})|(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})",
            message = "Invalid CNPJ format"
    )
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

    /**
     * Cross-field validation to ensure that at least one identification
     * document (CPF or CNPJ) is provided.
     *
     * @return true if either CPF or CNPJ is non-null and non-blank
     */
    @AssertTrue(message = "CPF or CNPJ must be provided")
    public boolean isCpfOrCnpjValid() {
        return (cpf != null && !cpf.isBlank()) ||
                (cnpj != null && !cnpj.isBlank());
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
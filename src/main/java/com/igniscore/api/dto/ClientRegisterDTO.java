package com.igniscore.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;

/**
 * Data Transfer Object (DTO) used for client registration requests.
 *
 * <p>Encapsulates input data required to create a {@link com.igniscore.api.model.Client}
 * and defines validation constraints to ensure data integrity before reaching
 * the service layer.
 *
 * <p><strong>Validation strategy:</strong>
 * <ul>
 *     <li>Field-level validation using Jakarta Bean Validation annotations</li>
 *     <li>Cross-field validation to enforce business rules (CPF or CNPJ requirement)</li>
 * </ul>
 *
 * <p><strong>Domain constraints:</strong>
 * <ul>
 *     <li>At least one of CPF or CNPJ must be provided</li>
 *     <li>CPF and CNPJ accept both numeric-only and formatted inputs</li>
 *     <li>Phone number must contain 10 or 11 digits (no formatting)</li>
 * </ul>
 *
 * <p><strong>Notes:</strong>
 * <ul>
 *     <li>This DTO is intended for input only (write operations)</li>
 *     <li>Does not contain business logic beyond validation rules</li>
 * </ul>
 */
public class ClientRegisterDTO {

    /**
     * Client name.
     *
     * <p>Must not be null or blank.
     */
    @NotBlank(message = "Name is required")
    private String name;

    /**
     * Client email address.
     *
     * <p>Must follow a valid email format if provided.
     */
    @Email(message = "Invalid email")
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
     * <p>Must contain 10 or 11 digits (area code + number), without formatting.
     */
    @Pattern(
            regexp = "\\d{10,11}",
            message = "Phone must contain 10 or 11 digits"
    )
    private String phone;

    /**
     * State registration (Inscrição Estadual).
     *
     * <p>Optional field used for tax identification at the state level.
     */
    private String ie;

    /**
     * Federative unit (state) associated with the state registration.
     *
     * <p>Typically a two-letter UF code (e.g., SP, RJ).
     */
    private String ufIe;

    /**
     * Additional notes or observations about the client.
     *
     * <p>Free-form text field.
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
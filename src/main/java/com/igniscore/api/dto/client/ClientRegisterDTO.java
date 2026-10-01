package com.igniscore.api.dto.client;

import com.igniscore.api.validation.ValidCNPJ;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object (DTO) used for client registration requests.
 *
 * <p>All fields are optional and may be null or blank.
 */
public class ClientRegisterDTO {

    private String name;

    private String legal;

    @Email(message = "Invalid email")
    private String email;

    @ValidCNPJ
    private String cnpj;

    @Pattern(
            regexp = "(\\d{11})|(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})",
            message = "Invalid CPF format"
    )
    private String cpf;

    @Pattern(
            regexp = "\\d{0,11}",
            message = "Phone must contain 10 or 11 digits"
    )
    private String phone;

    private String ie;

    private String ufIe;

    private String obs;

    public ClientRegisterDTO(
            String name,
            String legal,
            String email,
            String cnpj,
            String cpf,
            String phone,
            String ie,
            String ufIe,
            String obs
    ) {
        this.name = name;
        this.legal = legal;
        this.email = email;
        this.cnpj = cnpj;
        this.cpf = cpf;
        this.phone = phone;
        this.ie = ie;
        this.ufIe = ufIe;
        this.obs = obs;
    }

    public ClientRegisterDTO() {
    }

    public String getName() {
        return name;
    }

    public String getLegal() {
        return legal;
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

    public void setLegal(String legal) {
        this.legal = legal;
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
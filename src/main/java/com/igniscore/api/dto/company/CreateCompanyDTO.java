package com.igniscore.api.dto.company;

import com.igniscore.api.validation.ValidCNPJ;
import jakarta.validation.constraints.NotBlank;

public class CreateCompanyDTO {

    private String name;

    @NotBlank(message = "CNPJ é obrigatório")
    @ValidCNPJ
    private String cnpj;

    private String ie;

    private String ufIe;

    private String email;

    private String phone;

    public CreateCompanyDTO(String name, String cnpj, String ie, String ufIe, String email, String phone) {
        this.name = name;
        this.cnpj = cnpj;
        this.ie = ie;
        this.ufIe = ufIe;
        this.email = email;
        this.phone = phone;
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

package com.igniscore.api.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used for client registration requests.
 *
 * <p>All fields are optional and may be null or blank.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClientRegisterDTO {

    private String name;

    private String legal;

    private String email;

    private String cnpj;

    private String cpf;

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
}
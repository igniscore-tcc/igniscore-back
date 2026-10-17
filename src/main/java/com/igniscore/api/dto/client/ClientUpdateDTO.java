package com.igniscore.api.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

/**
 * Data Transfer Object (DTO) used for client update operations.
 *
 * <p>Represents a mutable payload where fields are optional and may be
 * partially provided.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
     * Updated client legal name.
     */
    private String legal;

    /**
     * Updated client email address.
     */
    private String email;

    /**
     * Brazilian CNPJ (Cadastro Nacional da Pessoa Jurídica).
     */
    private String cnpj;

    /**
     * Brazilian CPF (Cadastro de Pessoas Físicas).
     */
    private String cpf;

    /**
     * Client phone number.
     */
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
}
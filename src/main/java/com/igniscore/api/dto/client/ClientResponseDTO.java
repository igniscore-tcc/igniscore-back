package com.igniscore.api.dto.client;

import com.igniscore.api.dto.company.CompanyResponseDTO;
import com.igniscore.api.model.Client;

import java.io.Serial;
import java.io.Serializable;

public record ClientResponseDTO(
        Integer id,
        Integer number,
        String name,
        String cnpj,
        String email,
        String phone,
        String ie,
        String ufIe,
        String obs,
        String cpf,
        CompanyResponseDTO company
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClientResponseDTO(Client client) {
        this(
                client.getId(),
                client.getNumber(),
                client.getName(),
                client.getCnpj(),
                client.getEmail(),
                client.getPhone(),
                client.getIe(),
                client.getUfIe(),
                client.getObs(),
                client.getCpf(),
                client.getCompany() != null ? new CompanyResponseDTO(client.getCompany()) : null
        );
    }
}

package com.igniscore.api.dto.company;

import com.igniscore.api.model.Company;

import java.io.Serial;
import java.io.Serializable;

public record CompanyResponseDTO(
    Integer id,
    String name,
    String cnpj,
    String ie,
    String ufIe,
    String email,
    String phone
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public CompanyResponseDTO(Company company) {
        this(
                company.getId(),
                company.getName(),
                company.getCnpj(),
                company.getIe(),
                company.getUfIe(),
                company.getEmail(),
                company.getPhone()
        );
    }
}

package com.igniscore.api.dto.company;

import com.igniscore.api.validation.ValidCNPJ;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCompanyDTO {

    private String name;

    @NotBlank(message = "CNPJ é obrigatório")
    @ValidCNPJ
    private String cnpj;

    private String ie;

    private String ufIe;

    private String email;

    private String phone;
}

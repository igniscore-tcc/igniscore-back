package com.igniscore.api.repository;

import com.igniscore.api.dto.CreateCompanyDTO;
import com.igniscore.api.model.Company;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class CompanyRepositoryTest {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Should get Company successfully")
    void findByCnpjCase1() {
        String cnpj = "0000000000";
        CreateCompanyDTO dto = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        createCompany(dto);

        Optional<Company> result = this.companyRepository.findByCnpj(cnpj);

        System.out.println(result);

        assertThat(result.isPresent()).isTrue();
    }

    private Company createCompany(CreateCompanyDTO dto) {
        Company company = new Company(dto);

        this.entityManager.persist(company);

        return company;

    }
}
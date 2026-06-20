package com.igniscore.api.repository;

import com.igniscore.api.dto.client.ClientRegisterDTO;
import com.igniscore.api.dto.company.CreateCompanyDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void findByCompanyAndDeletedAtIsNull() {

        String cnpj = "23.608.666/0001-22";
        CreateCompanyDTO dtoCompany = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dtoCompany);

        Client client = createClient(company);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<Client> result = this.clientRepository.findByCompanyAndDeletedAtIsNull(company, pageable);

        assertThat(result.getContent())
                .hasSize(1)
                .contains(client);
    }

    @Test
    void findByIdAndCompanyAndDeletedAtIsNull() {
        String cnpj = "23.608.666/0001-22";
        CreateCompanyDTO dtoCompany = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");

        Company company = createCompany(dtoCompany);

        Client client = createClient(company);

        Optional<Client> result =  this.clientRepository.findByIdAndCompanyAndDeletedAtIsNull(client.getId(), company);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(client);

    }

    @Test
    void shouldReturnOnlyClientsFromCorrectCompany() {

        String cnpj = "23.608.666/0001-22";
        CreateCompanyDTO dtoCompany = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");
        Company company1 = createCompany(dtoCompany);

        Company company2 = createCompany(
                new CreateCompanyDTO(
                        "Outra",
                        "11.111.111/0001-11",
                        "12345674",
                        "SP",
                        "outra@gmail.com",
                        "999999999"
                )
        );

        Client client1 = createClient(company1);

        createClient(company2);

        Pageable pageable = PageRequest.of(0,10);

        Page<Client> result =
                clientRepository.findByCompanyAndDeletedAtIsNull(company1, pageable);

        assertThat(result.getContent())
                .hasSize(1)
                .contains(client1);
    }


    @Test
    void shouldReturnEmptyWhenClientIdDoesNotExist() {

        String cnpj = "23.608.666/0001-22";
        CreateCompanyDTO dtoCompany = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");
        Company company = createCompany(dtoCompany);

        Optional<Client> result =
                clientRepository.findByIdAndCompanyAndDeletedAtIsNull(999, company);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldNotReturnClientWhenCompanyIsWrong() {

        String cnpj = "23.608.666/0001-22";
        CreateCompanyDTO dtoCompany = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");
        Company company1 = createCompany(dtoCompany);

        Company company2 = createCompany(
                new CreateCompanyDTO(
                        "Outra",
                        "11.111.111/0001-11",
                        "1234567",
                        "SP",
                        "outra@gmail.com",
                        "999999999"
                )
        );

        Client client = createClient(company1);

        Optional<Client> result =
                clientRepository.findByIdAndCompanyAndDeletedAtIsNull(
                        client.getId(),
                        company2
                );

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnPaginatedClients() {

        String cnpj = "23.608.666/0001-22";
        CreateCompanyDTO dtoCompany = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");
        Company company = createCompany(dtoCompany);

        createClient(company);
        createClient(company);
        createClient(company);

        Pageable pageable = PageRequest.of(0,2);

        Page<Client> result =
                clientRepository.findByCompanyAndDeletedAtIsNull(company, pageable);

        assertThat(result.getContent()).hasSize(2);

        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void shouldReturnClientsOrderedById() {

        String cnpj = "23.608.666/0001-22";
        CreateCompanyDTO dtoCompany = new CreateCompanyDTO("IgnisCore", cnpj, "12345678", "SP", "igniscore@tcc.com", "0000000000");
        Company company = createCompany(dtoCompany);

        Client first = createClient(company);
        Client second = createClient(company);

        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(Sort.Direction.ASC, "id")
        );

        Page<Client> result =
                clientRepository.findByCompanyAndDeletedAtIsNull(company, pageable);

        assertThat(result.getContent().get(0).getId())
                .isLessThan(result.getContent().get(1).getId());
    }

    private Company createCompany(CreateCompanyDTO dto) {
        Company company = new Company(dto);

        this.entityManager.persist(company);
        entityManager.flush();

        return company;

    }

    private Client createClient(Company company){
        ClientRegisterDTO dto = new ClientRegisterDTO(
                "Gabriel",
                "gabriel@gmail.com",
                "47.229.900/0001-79",
                "19997891756",
                "347.707.549.795",
                "SP",
                "anderson lindo"
        );

        Client anderson = new Client(dto, company);

        this.entityManager.persist(anderson);
        entityManager.flush();
        return anderson;

    }
}
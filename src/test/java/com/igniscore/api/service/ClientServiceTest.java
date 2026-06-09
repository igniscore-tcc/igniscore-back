package com.igniscore.api.service;

import com.igniscore.api.dto.client.ClientRegisterDTO;
import com.igniscore.api.dto.client.ClientUpdateDTO;
import com.igniscore.api.dto.company.CreateCompanyDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.repository.CompanyRepository;
import com.igniscore.api.repository.UserRepository;
import com.igniscore.api.utils.AuditUtils;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditUtils auditUtils;

    @Mock
    private EntityManager entityManager;

    @Mock
    private AuthenticatedUserService authUserService;

    @InjectMocks
    private ClientService clientService;

    @InjectMocks
    private CompanyService companyService;

    @Test
    @DisplayName("Should create a client with successfully")
    void shouldCreteClient() {

        CreateCompanyDTO dtoCompany = new CreateCompanyDTO(
                "IgnisCore",
                "71.963.415/0001-09",
                "572.754.780.502",
                "SP",
                "suporte@igniscore.com",
                "1935798593"
        );

        var company = new Company(dtoCompany);

        User user = new User(1);
        user.setCompany(company);

        given(authUserService.getUserOrThrow()).willReturn(user);

        given(authUserService.getCompanyOrThrow()).willReturn(company);

        var dtoClient = new ClientRegisterDTO(
                "Cliente do IgnisCore",
                "clienteignscore@gmail.com",
                "37201849000133",
                "99999999999",
                "411873849804",
                "SP",
                ""
        );

        given(clientRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        var client = clientService.store(dtoClient);

        assertNotNull(client);

        assertEquals(company, client.getCompany());
    }

    @Test
    @DisplayName("Should must successfully list all clients")
    void shouldFindAllClient() {

        CreateCompanyDTO dtoCompany = new CreateCompanyDTO(
                "IgnisCore",
                "71.963.415/0001-09",
                "572.754.780.502",
                "SP",
                "suporte@igniscore.com",
                "1935798593"
        );

        var company = new Company(dtoCompany);

        User user = new User(1);
        user.setCompany(company);

        given(authUserService.getUserOrThrow()).willReturn(user);

        given(authUserService.getCompanyOrThrow()).willReturn(company);

        var dtoClientOne = new ClientRegisterDTO(
                "Cliente do IgnisCore",
                "clienteignscore@gmail.com",
                "37201849000133",
                "99999999999",
                "411873849804",
                "SP",
                ""
        );

        var dtoClientTwo = new ClientRegisterDTO(
                "Cliente do IgnisCore",
                "clienteignscore@gmail.com",
                "37201849000133",
                "99999999999",
                "411873849804",
                "SP",
                ""
        );

        given(clientRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        var clientOne = clientService.store(dtoClientOne);
        var clienteTwo = clientService.store(dtoClientTwo);

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "id")
        );

        List<Client> clients = List.of(clientOne, clienteTwo);

        Page<Client> pageResult =
                new PageImpl<>(clients, pageable, clients.size());

        given(clientRepository.findByCompany(
                eq(company),
                eq(pageable)
        )).willReturn(pageResult);

        var clientes = clientService.findAll(pageable);

        assertNotNull(clientes);
        assertEquals(2, clientes.getClients().size());
    }

    @Test
    @DisplayName("Should need to find a client successfully")
    void shouldFindOneClient() {

        CreateCompanyDTO dtoCompany = new CreateCompanyDTO(
                "IgnisCore",
                "71.963.415/0001-09",
                "572.754.780.502",
                "SP",
                "suporte@igniscore.com",
                "1935798593"
        );

        var company = new Company(dtoCompany);

        User user = new User(1);
        user.setCompany(company);

        given(authUserService.getUserOrThrow()).willReturn(user);

        given(authUserService.getCompanyOrThrow()).willReturn(company);

        var dtoClient = new ClientRegisterDTO(
                "Cliente do IgnisCore",
                "clienteignscore@gmail.com",
                "37201849000133",
                "99999999999",
                "411873849804",
                "SP",
                ""
        );

        given(clientRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        var client = clientService.store(dtoClient);
        client.setId(1);
        client.setCompany(company);

        given((clientRepository.findByIdAndCompany(1, company))).willReturn(Optional.of(client));

        var clientFind = clientService.findById(1);

        assertNotNull(clientFind);
        assertEquals(1, clientFind.getId());
    }

    @Test
    @DisplayName("Should must delete a client by ID")
    void shouldDeleteByIDClient() {

        CreateCompanyDTO dtoCompany = new CreateCompanyDTO(
                "IgnisCore",
                "71.963.415/0001-09",
                "572.754.780.502",
                "SP",
                "suporte@igniscore.com",
                "1935798593"
        );

        var company = new Company(dtoCompany);

        User user = new User(1);
        user.setCompany(company);

        given(authUserService.getUserOrThrow()).willReturn(user);

        given(authUserService.getCompanyOrThrow()).willReturn(company);

        var dtoClient = new ClientRegisterDTO(
                "Cliente do IgnisCore",
                "clienteignscore@gmail.com",
                "37201849000133",
                "99999999999",
                "411873849804",
                "SP",
                ""
        );

        given(clientRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        var client = clientService.store(dtoClient);
        client.setId(1);
        client.setCompany(company);

        given((clientRepository.findByIdAndCompany(1, company))).willReturn(Optional.of(client));

        var clientFind = clientService.findById(1);

        doNothing().when(clientRepository).delete(clientFind);

        String delete = clientService.delete(clientFind.getId());

        assertNotNull(delete);
        assertEquals("Client successfully deleted." , delete);

        verify(clientRepository).delete(clientFind);
    }

    @Test
    @DisplayName("Should must update a client")
    void shouldUpdateClient() {

        CreateCompanyDTO dtoCompany = new CreateCompanyDTO(
                "IgnisCore",
                "71.963.415/0001-09",
                "572.754.780.502",
                "SP",
                "suporte@igniscore.com",
                "1935798593"
        );

        var company = new Company(dtoCompany);

        User user = new User(1);
        user.setCompany(company);

        given(authUserService.getUserOrThrow()).willReturn(user);

        given(authUserService.getCompanyOrThrow()).willReturn(company);

        var dtoClient = new ClientRegisterDTO(
                "Cliente do IgnisCore",
                "clienteignscore@gmail.com",
                "37201849000133",
                "99999999999",
                "411873849804",
                "SP",
                ""
        );

        given(clientRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        var client = clientService.store(dtoClient);
        client.setId(1);
        client.setCompany(company);

        given((clientRepository.findByIdAndCompany(1, company))).willReturn(Optional.of(client));

        var clientFind = clientService.findById(1);

        var dtoClientUpdate = new ClientUpdateDTO(
                1,
                "Cliente do IgnisCore",
                "clienteignscore@gmail.com",
                "37201849000133",
                "",
                "1325799356",
                "314.606.502.496",
                "SP",
                "Nova observação"
        );

        var clientUpdate = clientService.update(dtoClientUpdate);

        assertNotNull(clientUpdate);
        assertEquals(dtoClientUpdate.getObs(), clientUpdate.getObs());
    }
}
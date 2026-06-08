package com.igniscore.api.service;

import com.igniscore.api.dto.client.ClientRegisterDTO;
import com.igniscore.api.dto.company.CreateCompanyDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
}
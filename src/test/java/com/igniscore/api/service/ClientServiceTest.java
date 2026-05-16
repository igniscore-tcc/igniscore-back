package com.igniscore.api.service;

import com.igniscore.api.dto.ClientRegisterDTO;
import com.igniscore.api.model.Client;
import com.igniscore.api.model.Company;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.ClientRepository;
import com.igniscore.api.utils.AuditUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService - Testes Unitários")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AuthenticatedUserService authUserService;

    @Mock
    private AuditUtils auditUtils;

    @InjectMocks
    private ClientService clientService;

    private Company company;
    private User user;
    private Client client;
    private ClientRegisterDTO clientRegisterDTO;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1);
        company.setName("Test Company");
        company.setCnpj("12.345.678/0001-99");

        user = new User();
        user.setId(1);
        user.setEmail("user@test.com");
        user.setCompany(company);

        client = new Client();
        client.setId(1);
        client.setName("Test Client");
        client.setCpf("123.456.789-00");
        client.setCompany(company);

        clientRegisterDTO = new ClientRegisterDTO();
        clientRegisterDTO.setName("New Client");
        clientRegisterDTO.setCpf("987.654.321-00");
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void testCreateClientSuccess() {
        when(authUserService.getUserOrThrow()).thenReturn(user);
        when(authUserService.getCompanyOrThrow()).thenReturn(company);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client result = clientService.store(clientRegisterDTO);

        assertNotNull(result);
        assertEquals("Test Client", result.getName());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não autenticado")
    void testCreateClientWithoutAuthentication() {
        when(authUserService.getUserOrThrow())
                .thenThrow(new AccessDeniedException("User not authenticated"));

        assertThrows(AccessDeniedException.class, () -> clientService.store(clientRegisterDTO));
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void testFindClientByIdSuccess() {
        when(authUserService.getCompanyOrThrow()).thenReturn(company);
        when(clientRepository.findByIdAndCompany(1, company)).thenReturn(Optional.of(client));

        Client result = clientService.findById(1);

        assertNotNull(result);
        assertEquals("Test Client", result.getName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não encontrado")
    void testFindClientByIdNotFound() {
        when(authUserService.getCompanyOrThrow()).thenReturn(company);
        when(clientRepository.findByIdAndCompany(1, company)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> clientService.findById(1));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void testUpdateClientSuccess() {
        ClientRegisterDTO updateDTO = new ClientRegisterDTO();
        updateDTO.setName("Updated Client");

        when(authUserService.getUserOrThrow()).thenReturn(user);
        when(authUserService.getCompanyOrThrow()).thenReturn(company);
        when(clientRepository.findByIdAndCompany(1, company)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        clientService.update(1, updateDTO);

        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Deve deletar cliente com sucesso")
    void testDeleteClientSuccess() {
        when(authUserService.getUserOrThrow()).thenReturn(user);
        when(authUserService.getCompanyOrThrow()).thenReturn(company);
        when(clientRepository.findByIdAndCompany(1, company)).thenReturn(Optional.of(client));

        String result = clientService.delete(1);

        assertEquals("Client successfully deleted.", result);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    @DisplayName("Deve listar clientes com paginação")
    void testFindAllWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> clientPage = new PageImpl<>(List.of(client), pageable, 1);

        when(authUserService.getCompanyOrThrow()).thenReturn(company);
        when(clientRepository.findByCompany(company, pageable)).thenReturn(clientPage);

        var result = clientService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(clientRepository, times(1)).findByCompany(company, pageable);
    }

    @Test
    @DisplayName("Deve validar isolamento multi-tenant")
    void testMultiTenantIsolation() {
        Company otherCompany = new Company();
        otherCompany.setId(2);
        otherCompany.setName("Other Company");

        client.setCompany(otherCompany);

        when(authUserService.getCompanyOrThrow()).thenReturn(company);
        when(clientRepository.findByIdAndCompany(1, company)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> clientService.findById(1));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve validar CPF ou CNPJ obrigatório")
    void testValidateCpfOrCnpj() {
        clientRegisterDTO.setCpf(null);
        clientRegisterDTO.setCnpj(null);

        when(authUserService.getUserOrThrow()).thenReturn(user);
        when(authUserService.getCompanyOrThrow()).thenReturn(company);

        assertThrows(IllegalArgumentException.class, () -> clientService.store(clientRegisterDTO));
    }
}

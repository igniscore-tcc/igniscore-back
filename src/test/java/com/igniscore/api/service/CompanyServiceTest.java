package com.igniscore.api.service;

import com.igniscore.api.dto.company.CreateCompanyDTO;
import com.igniscore.api.model.User;
import com.igniscore.api.repository.CompanyRepository;
import com.igniscore.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticatedUserService authUserService;

    @InjectMocks
    private CompanyService companyService;

    @Test
    @DisplayName("Should create a company with successfully")
    void shouldCreateCompany() {

        CreateCompanyDTO dto = new CreateCompanyDTO(
                "IgnisCore",
                "71.963.415/0001-09",
                "572.754.780.502",
                "SP",
                "suporte@igniscore.com",
                "1935798593"
        );

        User user = new User(1);

        given(authUserService.getUserOrThrow()).willReturn(user);

        given(companyRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        var company = companyService.storeCompany(dto);

        assertNotNull(company);

        assertEquals("IgnisCore", company.getName());
        assertEquals("71.963.415/0001-09", company.getCnpj());

        assertEquals(company, user.getCompany());

        verify(authUserService).getUserOrThrow();
        verify(companyRepository).save(any());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should not create company when CNPJ is empty")
    void notShouldCreteCompany() {
        CreateCompanyDTO dto = new CreateCompanyDTO(
                "IgnisCore",
                "",
                "572.754.780.502",
                "SP",
                "suporte@igniscore.com",
                "1935798593"
        );

        User user = new User(1);

        given(authUserService.getUserOrThrow()).willReturn(user);

        assertThrows(
                IllegalArgumentException.class,
                () -> companyService.storeCompany(dto)
        );
    }
}
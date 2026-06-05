package com.igniscore.api.service;

import com.igniscore.api.dto.ExpirationDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.repository.ExpirationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpirationService {

    @Autowired
    private ExpirationRepository expirationRepository;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;


    public List<ExpirationDTO> getExpirations() {
        Company company = authenticatedUserService.getCompanyOrThrow();
        return expirationRepository.findExpirationsByCompanyId(company.getId());
    }
}

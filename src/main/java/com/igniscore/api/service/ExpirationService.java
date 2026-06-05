package com.igniscore.api.service;

import com.igniscore.api.dto.expiration.ExpirationDTO;
import com.igniscore.api.dto.expiration.ExpirationProjectionDTO;
import com.igniscore.api.dto.expiration.ExpirationStatus;
import com.igniscore.api.model.Company;
import com.igniscore.api.repository.ExpirationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpirationService {

    private final ExpirationRepository expirationRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ExpirationService(
            ExpirationRepository expirationRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.expirationRepository = expirationRepository;
        this.authenticatedUserService = authenticatedUserService;
    }


    public List<ExpirationDTO> getExpirations() {
        Company company = authenticatedUserService.getCompanyOrThrow();

        return expirationRepository
                .findExpirationsByCompanyId(company.getId())
                .stream()
                .map(this::mapExpiration)
                .toList();
    }

    public List<ExpirationDTO> getExpirationsByPeriod(
            LocalDate startDate,
            LocalDate endDate
    ) {
        Company company = authenticatedUserService.getCompanyOrThrow();

        return expirationRepository
                .findExpirationsByPeriod(
                        company.getId(),
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapExpiration)
                .toList();
    }

    public List<ExpirationDTO> getUpcomingExpirations(
            Integer days
    ) {
        Company company = authenticatedUserService.getCompanyOrThrow();

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return expirationRepository
                .findUpcomingExpirations(
                        company.getId(),
                        today,
                        endDate
                )
                .stream()
                .map(this::mapExpiration)
                .toList();
    }

    public List<ExpirationDTO> getExpirationsByClient(
            Integer clientId
    ) {
        Company company = authenticatedUserService.getCompanyOrThrow();

        return expirationRepository
                .findExpirationsByClient(
                        company.getId(),
                        clientId
                )
                .stream()
                .map(this::mapExpiration)
                .toList();
    }

    private ExpirationStatus calculateStatus(LocalDate dueDate) {

        LocalDate today = LocalDate.now();

        if (dueDate.isBefore(today)) {
            return ExpirationStatus.VENCIDO;
        }

        if (!dueDate.isAfter(today.plusDays(30))) {
            return ExpirationStatus.PROXIMO;
        }

        return ExpirationStatus.NORMAL;
    }

    private ExpirationDTO mapExpiration(
            ExpirationProjectionDTO expiration
    ) {
        return new ExpirationDTO(
                expiration.saleId(),
                expiration.clientName(),
                expiration.saleDate(),
                expiration.dueDate(),
                expiration.totalSale(),
                calculateStatus(expiration.dueDate())
        );
    }
}

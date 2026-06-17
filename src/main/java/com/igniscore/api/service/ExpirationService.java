package com.igniscore.api.service;

import com.igniscore.api.dto.expiration.ExpirationDTO;
import com.igniscore.api.dto.expiration.ExpirationPageDTO;
import com.igniscore.api.dto.expiration.ExpirationProjectionDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.repository.ExpirationRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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


    public ExpirationPageDTO getExpirations(
            Integer page,
            Integer size
    ) {
        Company company = authenticatedUserService.getCompanyOrThrow();

        Page<ExpirationProjectionDTO> result =
                expirationRepository.findExpirationsByCompanyId(
                        company.getId(),
                        PageRequest.of(page, size)
                );

        return new ExpirationPageDTO(
                result.getContent()
                        .stream()
                        .map(this::mapExpiration)
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    public ExpirationPageDTO getExpirationsByPeriod(
            LocalDate startDate,
            LocalDate endDate,
            Integer page,
            Integer size
    ) {
        Company company = authenticatedUserService.getCompanyOrThrow();

        Page<ExpirationProjectionDTO> result =
                expirationRepository.findExpirationsByPeriod(
                        company.getId(),
                        startDate,
                        endDate,
                        PageRequest.of(page, size)
                );

        return new ExpirationPageDTO(
                result.getContent()
                        .stream()
                        .map(this::mapExpiration)
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    public ExpirationPageDTO getUpcomingExpirations(
            Integer days,
            Integer page,
            Integer size
    ) {
        Company company = authenticatedUserService.getCompanyOrThrow();

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        Page<ExpirationProjectionDTO> result =
                expirationRepository.findUpcomingExpirations(
                        company.getId(),
                        today,
                        endDate,
                        PageRequest.of(page, size)
                );

        return new ExpirationPageDTO(
                result.getContent()
                        .stream()
                        .map(this::mapExpiration)
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    public ExpirationPageDTO getExpirationsByClient(
            Integer clientId,
            Integer page,
            Integer size
    ) {
        Company company = authenticatedUserService.getCompanyOrThrow();

        Page<ExpirationProjectionDTO> result =
                expirationRepository.findExpirationsByClient(
                        company.getId(),
                        clientId,
                        PageRequest.of(page, size)
                );

        return new ExpirationPageDTO(
                result.getContent()
                        .stream()
                        .map(this::mapExpiration)
                        .toList(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    private ExpirationDTO mapExpiration(
            ExpirationProjectionDTO expiration
    ) {
        return new ExpirationDTO(
                expiration.expirationId(),
                expiration.saleId(),
                expiration.clientName(),
                expiration.saleDate(),
                expiration.dueDate(),
                expiration.totalSale(),
                expiration.status()
        );
    }
}

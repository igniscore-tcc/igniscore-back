package com.igniscore.api.service;

import com.igniscore.api.dto.expiration.ExpirationDTO;
import com.igniscore.api.dto.expiration.ExpirationPageDTO;
import com.igniscore.api.dto.expiration.ExpirationProjectionDTO;
import com.igniscore.api.model.Company;
import com.igniscore.api.repository.ExpirationRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    @Cacheable(
            value = "expirations",
            key = "@cacheKeyService.expirationsKey(T(org.springframework.data.domain.PageRequest).of(#page, #size))"
    )
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

    @Cacheable(
            value = "expirationsByPeriod",
            key = "@cacheKeyService.expirationsByPeriodKey(#startDate, #endDate, T(org.springframework.data.domain.PageRequest).of(#page, #size))"
    )
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

    @Cacheable(
            value = "upcomingExpirations",
            key = "@cacheKeyService.upcomingExpirationsKey(#days, T(org.springframework.data.domain.PageRequest).of(#page, #size))"
    )
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

    @Cacheable(
            value = "expirationsByClient",
            key = "@cacheKeyService.expirationsByClientKey(#clientId, T(org.springframework.data.domain.PageRequest).of(#page, #size))"
    )
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
                expiration.expirationNumber(),
                expiration.saleId(),
                expiration.clientName(),
                expiration.saleDate(),
                expiration.dueDate(),
                expiration.totalSale(),
                expiration.status()
        );
    }
}
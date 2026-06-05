package com.igniscore.api.controller;

import com.igniscore.api.dto.expiration.ExpirationDTO;
import com.igniscore.api.service.ExpirationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ExpirationController {

    private final ExpirationService expirationService;


    public ExpirationController(
            ExpirationService expirationService
    ) {
        this.expirationService = expirationService;
    }

    @QueryMapping
    public List<ExpirationDTO> expirations() {
        return expirationService.getExpirations();
    }

    @QueryMapping
    public List<ExpirationDTO> expirationsByPeriod(@Argument LocalDate startDate, @Argument LocalDate endDate) {
        return expirationService.getExpirationsByPeriod(startDate, endDate);
    }

    @QueryMapping
    public List<ExpirationDTO> upcomingExpirations(
            @Argument Integer days
    ) {
        return expirationService.getUpcomingExpirations(days);
    }

    @QueryMapping
    public List<ExpirationDTO> expirationsByClient(
            @Argument Integer clientId
    ) {
        return expirationService.getExpirationsByClient(clientId);
    }
}
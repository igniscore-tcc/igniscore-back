package com.igniscore.api.controller;

import com.igniscore.api.dto.expiration.ExpirationDTO;
import com.igniscore.api.dto.expiration.ExpirationPageDTO;
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
    public ExpirationPageDTO expirations(
            @Argument Integer page,
            @Argument Integer size
    ) {
        return expirationService.getExpirations(page, size);
    }

    @QueryMapping
    public ExpirationPageDTO expirationsByPeriod(@Argument LocalDate startDate,
                                                   @Argument LocalDate endDate,
                                                   @Argument Integer page,
                                                   @Argument Integer size) {
        return expirationService.getExpirationsByPeriod(startDate, endDate, page, size);
    }

    @QueryMapping
    public ExpirationPageDTO upcomingExpirations(
            @Argument Integer days,
            @Argument Integer page,
            @Argument Integer size
    ) {
        return expirationService.getUpcomingExpirations(
                days,
                page,
                size
        );
    }

    @QueryMapping
    public ExpirationPageDTO expirationsByClient(
            @Argument Integer clientId,
            @Argument Integer page,
            @Argument Integer size
    ) {
        return expirationService.getExpirationsByClient(
                clientId,
                page,
                size
        );
    }
}
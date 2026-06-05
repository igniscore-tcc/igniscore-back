package com.igniscore.api.controller;

import com.igniscore.api.dto.ExpirationDTO;
import com.igniscore.api.service.ExpirationService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

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
}
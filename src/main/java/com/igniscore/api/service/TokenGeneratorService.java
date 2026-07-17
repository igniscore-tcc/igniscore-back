package com.igniscore.api.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TokenGeneratorService {

    private final SecureRandom random = new SecureRandom();

    public String generateVerificationCode() {
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}

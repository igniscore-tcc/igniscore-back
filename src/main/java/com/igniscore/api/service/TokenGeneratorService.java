package com.igniscore.api.service;

import java.security.SecureRandom;

public class TokenGeneratorService {

    private final SecureRandom random = new SecureRandom();

    public String generateVerificationCode() {
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}

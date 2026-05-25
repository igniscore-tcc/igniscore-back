package com.igniscore.api.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CnpjValidatorTest {

    @Test
    void shouldValidateValidCnpj() {

        assertTrue(CnpjValidator.isValid("11.444.777/0001-61"));
    }

    @Test
    void shouldInvalidateWrongCnpj() {

        assertFalse(CnpjValidator.isValid("11.444.777/0001-00"));
    }

    @Test
    void shouldInvalidateRepeatedNumbers() {

        assertFalse(CnpjValidator.isValid("11.111.111/1111-11"));
    }

    @Test
    void shouldNormalizeCnpj() {

        String normalized = CnpjValidator.normalize("11.444.777/0001-61");

        assertEquals("11444777000161", normalized);
    }
}
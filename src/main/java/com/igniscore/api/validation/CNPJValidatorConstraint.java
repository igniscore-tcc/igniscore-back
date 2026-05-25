package com.igniscore.api.validation;

import com.igniscore.api.utils.CnpjValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CNPJValidatorConstraint
        implements ConstraintValidator<ValidCNPJ, String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        if (value == null || value.isBlank()) {
            return true;
        }

        return CnpjValidator.isValid(value);
    }
}
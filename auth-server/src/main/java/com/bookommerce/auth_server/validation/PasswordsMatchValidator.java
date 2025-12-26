package com.bookommerce.auth_server.validation;

import com.bookommerce.auth_server.dto.request.RegistrationRequestDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegistrationRequestDto> {

    //@formatter:off
    @Override
    public boolean isValid(RegistrationRequestDto value, ConstraintValidatorContext context) {
        String password = value.password();
        String confirmPassword = value.confirmPassword();
        return password.equals(confirmPassword);
    }

}

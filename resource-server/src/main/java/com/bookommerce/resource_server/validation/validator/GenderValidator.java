package com.bookommerce.resource_server.validation.validator;

import java.util.stream.Stream;

import com.bookommerce.resource_server.constant.Genders;
import com.bookommerce.resource_server.utils.ValidationUtils;
import com.bookommerce.resource_server.validation.annotation.Gender;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// @formatter:off
public class GenderValidator implements ConstraintValidator<Gender, String> {

    @Override
    public boolean isValid(String gender, ConstraintValidatorContext context) {
        if (gender == null) {
            return true;
        }

        boolean isValidGender = Stream.of(Genders.values())
            .anyMatch(genderEnum -> genderEnum.name().equals(gender));
        if (!isValidGender) {
            ValidationUtils.buildGlobalValidationMessage("Gender: [" + gender + "] is not allowed", context);
            return false;
        }

        return true;
    }
}

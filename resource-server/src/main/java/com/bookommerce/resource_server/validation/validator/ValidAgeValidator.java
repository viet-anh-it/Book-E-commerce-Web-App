package com.bookommerce.resource_server.validation.validator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import com.bookommerce.resource_server.utils.ValidationUtils;
import com.bookommerce.resource_server.validation.annotation.ValidAge;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidAgeValidator implements ConstraintValidator<ValidAge, LocalDate> {

    int minAge;
    int maxAge;

    @Override
    public void initialize(ValidAge constraintAnnotation) {
        this.minAge = constraintAnnotation.min();
        this.maxAge = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate now = LocalDate.now(Clock.system(ZoneId.of("Asia/Ho_Chi_Minh")));
        int age = Period.between(value, now).getYears();
        if (age < this.minAge || age > this.maxAge) {
            ValidationUtils.buildGlobalValidationMessage("Age: [" + age + "] is not allowed", context);
            return false;
        }
        return true;
    }
}

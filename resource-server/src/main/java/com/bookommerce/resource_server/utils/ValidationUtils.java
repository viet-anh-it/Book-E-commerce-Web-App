package com.bookommerce.resource_server.utils;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// @formatter:off
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {
    
    public static BindingResult createBindingResult(Object target, String objectName, String field, String defaultMessage) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, objectName);
        bindingResult.rejectValue(field, "", defaultMessage);
        return bindingResult;
    }

    public static void buildGlobalValidationMessage(String message, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addConstraintViolation();
    }

    public static void buildValidationMessage(String field, String message, ConstraintValidatorContext context) {
        if (field == null) {
            field = "";
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(field)
            .addConstraintViolation();
    }
}

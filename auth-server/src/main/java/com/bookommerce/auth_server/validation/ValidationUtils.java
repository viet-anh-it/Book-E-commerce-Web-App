package com.bookommerce.auth_server.validation;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

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
}

package com.bookommerce.resource_server.utils;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationUtils {
    //@formatter:off
    public static BindingResult createBindingResult(Object target, String objectName, String field, String defaultMessage) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, objectName);
        bindingResult.rejectValue(field, "", defaultMessage);
        return bindingResult;
    }
}

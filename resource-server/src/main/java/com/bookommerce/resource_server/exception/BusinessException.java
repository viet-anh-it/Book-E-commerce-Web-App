package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class BusinessException extends RuntimeException {
    BindingResult bindingResult;
}

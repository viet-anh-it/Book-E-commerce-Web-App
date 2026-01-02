package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class IllegalResourceStateException extends BusinessException {
    public IllegalResourceStateException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

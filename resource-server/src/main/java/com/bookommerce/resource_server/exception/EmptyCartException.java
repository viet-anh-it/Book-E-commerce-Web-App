package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class EmptyCartException extends BusinessException {
    public EmptyCartException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

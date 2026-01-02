package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

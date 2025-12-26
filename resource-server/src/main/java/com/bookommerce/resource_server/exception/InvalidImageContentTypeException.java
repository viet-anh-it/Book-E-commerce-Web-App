package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class InvalidImageContentTypeException extends BusinessException {
    public InvalidImageContentTypeException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

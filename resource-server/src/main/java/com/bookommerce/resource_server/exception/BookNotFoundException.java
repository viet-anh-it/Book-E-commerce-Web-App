package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class BookNotFoundException extends BusinessException {
    public BookNotFoundException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

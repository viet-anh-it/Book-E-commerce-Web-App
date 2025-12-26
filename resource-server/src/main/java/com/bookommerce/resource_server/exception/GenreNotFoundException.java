package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class GenreNotFoundException extends BusinessException {
    public GenreNotFoundException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

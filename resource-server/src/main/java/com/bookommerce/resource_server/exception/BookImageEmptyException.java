package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class BookImageEmptyException extends BusinessException {
    public BookImageEmptyException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

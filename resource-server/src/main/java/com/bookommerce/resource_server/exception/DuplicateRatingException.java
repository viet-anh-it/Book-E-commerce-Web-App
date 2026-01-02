package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class DuplicateRatingException extends BusinessException {
    public DuplicateRatingException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

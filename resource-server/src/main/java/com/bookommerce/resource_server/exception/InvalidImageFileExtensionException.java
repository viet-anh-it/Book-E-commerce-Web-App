package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class InvalidImageFileExtensionException extends BusinessException {
    public InvalidImageFileExtensionException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

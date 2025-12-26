package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class CartItemNotFoundException extends BusinessException {
    public CartItemNotFoundException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

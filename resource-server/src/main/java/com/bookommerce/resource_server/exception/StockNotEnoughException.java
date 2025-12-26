package com.bookommerce.resource_server.exception;

import org.springframework.validation.BindingResult;

public class StockNotEnoughException extends BusinessException {
    public StockNotEnoughException(BindingResult bindingResult) {
        super(bindingResult);
    }
}

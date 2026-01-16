package com.bookommerce.auth_server.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bookommerce.auth_server.dto.response.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //@formatter:off
    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        EmailAlreadyExistedException.class
    })
    
    public ResponseEntity<ApiErrorResponse<Map<String, Object>>> handleValidationException(Exception ex) {
        BindingResult bindingResult = null;
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            bindingResult = methodArgumentNotValidException.getBindingResult();
        } else if (ex instanceof EmailAlreadyExistedException) {
            EmailAlreadyExistedException emailAlreadyExistedException = (EmailAlreadyExistedException) ex;
            bindingResult = emailAlreadyExistedException.getBindingResult();
        }
        // Extract global errors
        Map<String, Object> error = new HashMap<>();
        List<String> globalErrors = bindingResult.getGlobalErrors().stream().map(globalError -> globalError.getDefaultMessage()).toList();
        error.put("globalErrors", globalErrors);
        
        // Extract field errors
        Map<String, List<String>> fieldErrors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            fieldErrors.computeIfAbsent(fieldError.getField(), k -> new ArrayList<>()).add(fieldError.getDefaultMessage());
        });
        fieldErrors.forEach((field, messages) -> messages.sort(null)); // Sort messages ASC
        error.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(ApiErrorResponse.<Map<String, Object>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .error(error)
                .build());
    }
}

package com.bookommerce.be_for_fe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @formatter:off
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(exception = {ClientAuthorizationException.class})
    public ResponseEntity<Void> handleClientAuthorizationException(
        ClientAuthorizationException clientAuthorizationException,
        HttpServletRequest request,
        HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

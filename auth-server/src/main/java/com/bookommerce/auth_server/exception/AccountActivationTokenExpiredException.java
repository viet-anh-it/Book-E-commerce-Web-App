package com.bookommerce.auth_server.exception;

public class AccountActivationTokenExpiredException extends RuntimeException {
    private String email;

    public AccountActivationTokenExpiredException(String message) {
        super(message);
    }

    public AccountActivationTokenExpiredException(String message, String email) {
        super(message);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}

package com.bookommerce.auth_server.dto.request;

import com.bookommerce.auth_server.validation.ValidationOrder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// @formatter:off
public record ResendAccountActivationEmailDto(
    @NotBlank(message = "A blank email is not allowed", groups = { ValidationOrder._1.class })
    @Email(message = "The current email's format is invalid", groups = { ValidationOrder._2.class })
    String email
) {}

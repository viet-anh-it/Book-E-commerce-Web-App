package com.bookommerce.auth_server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//@formatter:off
public record LoginRequestDto(
    @NotBlank(message = "1:Email must not be blank")
    @Email(message = "2:Email must have a valid format")
    String email,

    @NotBlank(message = "1:Password must not be blank")
    @Size(min = 6, message = "2:Password must be at least 6 characters long")
    String password
) {}

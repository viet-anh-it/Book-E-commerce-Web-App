package com.bookommerce.auth_server.dto.request;

import com.bookommerce.auth_server.validation.PasswordsMatch;
import com.bookommerce.auth_server.validation.ValidationOrder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//@formatter:off
@PasswordsMatch(groups = { ValidationOrder._2.class })
public record RegistrationRequestDto(
    @NotBlank(message = "1:Email must not be blank", groups = { ValidationOrder._1.class })
    @Email(message = "2:Email is invalid", groups = { ValidationOrder._1.class })
    String email,

    @NotBlank(message = "1:Password must not be blank", groups = { ValidationOrder._1.class })
    @Size(min = 6, message = "2:Password must be at least 6 characters long", groups = { ValidationOrder._1.class })
    String password,

    @NotBlank(message = "1:Confirm password must not be blank", groups = { ValidationOrder._1.class })
    @Size(min = 6, message = "2:Confirm password must be at least 6 characters long", groups = { ValidationOrder._1.class })
    String confirmPassword
) {}

package com.bookommerce.resource_server.dto.request;

import java.time.LocalDate;

import com.bookommerce.resource_server.validation.annotation.Gender;
import com.bookommerce.resource_server.validation.annotation.ValidAge;
import com.bookommerce.resource_server.validation.annotation.ValidationOrder;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

// @formatter:off
public record UpdateMyProfileRequestDto (
    String firstName,
    String lastName,

    @Pattern(regexp = "^0\\d{9}$", message = "Phone number format is invalid", groups = ValidationOrder._1.class)
    String phone,

    @Gender(groups = ValidationOrder._1.class)
    String gender,

    @Past(message = "Date of birth must be in the past", groups = ValidationOrder._1.class)
    @ValidAge(groups = ValidationOrder._2.class)
    LocalDate dob
) {}

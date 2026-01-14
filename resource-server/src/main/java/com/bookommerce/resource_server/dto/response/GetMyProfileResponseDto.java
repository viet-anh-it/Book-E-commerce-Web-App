package com.bookommerce.resource_server.dto.response;

import java.time.LocalDate;

import com.bookommerce.resource_server.constant.Genders;

// @formatter:off
public record GetMyProfileResponseDto (
    String lastName,
    String firstName,
    String phone,
    Genders gender,
    LocalDate dob,
    String avatarUrlPath
) {}

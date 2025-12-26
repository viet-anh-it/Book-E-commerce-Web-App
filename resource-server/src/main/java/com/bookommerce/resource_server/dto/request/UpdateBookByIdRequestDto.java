package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.NotBlank;

// @formatter:off
public record UpdateBookByIdRequestDto(
    @NotBlank(message = "Description must not be blank")
    String description
) {}

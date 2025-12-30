package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Min;

//@formatter:off
public record BookIdRequestDto(
    @Min(value = 0, message = "ID less than 0 is not allowed")
    long id
) {}

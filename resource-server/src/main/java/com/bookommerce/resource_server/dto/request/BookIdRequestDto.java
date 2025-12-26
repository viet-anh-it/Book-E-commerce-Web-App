package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Min;

//@formatter:off
public record BookIdRequestDto(
    @Min(value = 0, message = "Book ID must be greater than or equal to 0")
    long id
) {}

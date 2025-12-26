package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Min;

//@formatter:off
public record UpdateCartItemQuantityRequestDto(
    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity
) {}

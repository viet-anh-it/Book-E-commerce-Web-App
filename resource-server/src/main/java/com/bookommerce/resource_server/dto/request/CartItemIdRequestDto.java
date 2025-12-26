package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Min;

//@formatter:off
public record CartItemIdRequestDto(
    @Min(value = 0, message = "Cart item ID must be greater than or equal to 0")
    long cartItemId
) {}

package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

//@formatter:off
public record AddToCartRequestDto(

    @PositiveOrZero(message = "Book ID must be greater than or equal to 0")
    long bookId,

    @Min(value = 1, message = "Quantity must be greater than or equal to 1")
    int quantity
) {}

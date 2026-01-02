package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;

// @formatter:off
public record CreateRatingRequestDto(
        @Min(value = 1, message = "Rating point less than 1 is not allowed")
        @Max(value = 5, message = "Rating point greater than 5 is not allowed")
        int point,

        @NotBlank(message = "A blank comment is not allowed")
        String comment,

        @Min(value = 0, message = "Book ID less than 0 is not allowed")
        long bookId
) {}

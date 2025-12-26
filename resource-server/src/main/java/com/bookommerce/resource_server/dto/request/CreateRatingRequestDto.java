package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;

// @formatter:off
public record CreateRatingRequestDto(
        @NotBlank(message = "Rater must not be blank")
        String rater,

        @Min(value = 1, message = "Rating point must be greater than or equal to 1")
        @Max(value = 5, message = "Rating point must be less than or equal to 5")
        int point,

        @NotBlank(message = "Comment must not be blank")
        String comment,

        @Min(value = 0, message = "Book ID must be greater than or equal to 0")
        long bookId
) {}

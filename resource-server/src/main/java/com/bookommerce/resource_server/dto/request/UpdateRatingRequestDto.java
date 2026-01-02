package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// @formatter:off
public record UpdateRatingRequestDto (
    @Min(value = 0, message = "ID less than 0 is not allowed")
    long id,

    @Min(value = 1, message = "Point less than 1 is not allowed")
    @Max(value = 5, message = "Point more than 5 is not allowed")
    int point,

    @NotBlank(message = "A blank comment is not allowed")
    String comment
) {}

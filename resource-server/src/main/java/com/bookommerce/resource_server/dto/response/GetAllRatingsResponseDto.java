package com.bookommerce.resource_server.dto.response;

import java.time.Instant;

// @formatter:off
public record GetAllRatingsResponseDto(
    Rating rating,
    Book book
) {
    public record Rating(
        long id,
        String rater,
        int point,
        String comment,
        Instant createdAt,
        boolean approved
    ) {}

    public record Book(
        long id,
        String title
    ) {}
}

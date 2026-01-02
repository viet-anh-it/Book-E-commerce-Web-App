package com.bookommerce.resource_server.dto.response;

import java.time.Instant;

// @formatter:off
public record GetRatingsByBookIdResponseDto (
    long id,
    String rater,
    int point,
    String comment,
    Instant createdAt
) {}

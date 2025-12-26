package com.bookommerce.resource_server.dto.response;

// @formatter:off
public record GetAllBooksResponseDto(
    long id,
    String title,
    String author,
    String thumbnailUrlPath,
    double price,
    double rating
) {}

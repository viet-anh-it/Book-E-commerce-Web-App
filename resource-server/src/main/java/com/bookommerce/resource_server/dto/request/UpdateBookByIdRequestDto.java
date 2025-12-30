package com.bookommerce.resource_server.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.bookommerce.resource_server.validation.ValidUpdateBookImage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

// @formatter:off
@ValidUpdateBookImage
public record UpdateBookByIdRequestDto(

    @NotBlank(message = "A blank title is not allowed")
    String title,

    @NotBlank(message = "A blank author name is not allowed")
    String author,

    @Min(value = 0, message = "Price less than 0 is not allowed")
    @Max(value = 1_000_000, message = "Price greater than 1000000 is not allowed")
    double price,

    @Min(value = 1, message = "Stock less than 1 is not allowed")
    int stock,

    String imageUrlPath,
    MultipartFile image,

    @NotBlank(message = "A blank description is not allowed")
    String description,

    @PositiveOrZero(message = "Genre ID less than 0 is not allowed")
    long genreId
) {}

package com.bookommerce.resource_server.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.bookommerce.resource_server.validation.ValidBookImage;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateBookRequestDto {

    @NotBlank(message = "Book title must not be empty")
    String title;

    @NotBlank(message = "Book author must not be empty")
    String author;

    @Min(value = 0, message = "Book price must be greater than or equal to 0")
    @Max(value = 1_000_000, message = "Book price must be less than or equal to 1000000")
    double price;

    @NotBlank(message = "Book description must not be empty")
    String description;

    @Min(value = 1, message = "Book stock must be greater than or equal to 1")
    int stock;

    @ValidBookImage
    MultipartFile image;

    @PositiveOrZero(message = "Genre ID must be positive or zero")
    long genreId;
}

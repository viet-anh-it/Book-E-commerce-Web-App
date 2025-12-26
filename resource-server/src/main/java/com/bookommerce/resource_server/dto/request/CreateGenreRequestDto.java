package com.bookommerce.resource_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateGenreRequestDto {

    @NotBlank(message = "Genre name must not be empty")
    String name;

    @NotBlank(message = "Genre description must not be empty")
    String description;
}

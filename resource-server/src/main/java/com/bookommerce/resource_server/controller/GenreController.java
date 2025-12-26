package com.bookommerce.resource_server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.resource_server.dto.request.CreateGenreRequestDto;
import com.bookommerce.resource_server.dto.response.ApiSuccessResponse;
import com.bookommerce.resource_server.entity.Genre;
import com.bookommerce.resource_server.service.GenreService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * REST controller for managing genres.
 * <p>
 * This controller provides endpoints for creating and retrieving book genres.
 * </p>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {

    GenreService genreService;

    /**
     * Creates a new genre.
     *
     * @param requestDto the DTO containing the details of the genre to be created.
     * @return a {@link ResponseEntity} containing an {@link ApiSuccessResponse}
     *         with no data,
     *         indicating successful creation (HTTP 201 Created).
     */
    @PostMapping("/genres")
    public ResponseEntity<ApiSuccessResponse<Void>> createGenre(@RequestBody @Valid CreateGenreRequestDto requestDto) {
        this.genreService.createGenre(requestDto);
        ApiSuccessResponse<Void> apiSuccessResponse = ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("Genre created successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiSuccessResponse);
    }

    /**
     * Retrieves all available genres.
     *
     * @return a {@link ResponseEntity} containing an {@link ApiSuccessResponse}
     *         with a list of {@link Genre} objects.
     */
    @GetMapping("/genres")
    public ResponseEntity<ApiSuccessResponse<List<Genre>>> getAllGenres() {
        List<Genre> genres = this.genreService.getAllGenres();
        ApiSuccessResponse<List<Genre>> apiSuccessResponse = ApiSuccessResponse.<List<Genre>>builder()
                .status(HttpStatus.OK.value())
                .message("Genres fetched successfully")
                .data(genres)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiSuccessResponse);
    }
}

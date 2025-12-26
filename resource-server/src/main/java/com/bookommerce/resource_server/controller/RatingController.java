package com.bookommerce.resource_server.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.resource_server.dto.request.CreateRatingRequestDto;
import com.bookommerce.resource_server.dto.request.GetRatingsByBookIdRequestDto;
import com.bookommerce.resource_server.dto.response.ApiSuccessResponse;
import com.bookommerce.resource_server.dto.response.PagingAndSortingMetadata;
import com.bookommerce.resource_server.entity.Rating;
import com.bookommerce.resource_server.service.RatingService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * REST controller for managing ratings.
 * <p>
 * This controller provides endpoints for submitting ratings for books.
 * </p>
 */
@RestController
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RatingController {

    RatingService ratingService;

    //@formatter:off
    /**
     * Submits a rating for a book.
     *
     * @param createRatingRequestDto the DTO containing the rating details.
     * @return a {@link ResponseEntity} containing an {@link ApiSuccessResponse} with no data,
     *         indicating successful creation (HTTP 201 Created).
     */
    @PostMapping("/ratings")
    public ResponseEntity<ApiSuccessResponse<Void>> createRating(
            @RequestBody @Valid CreateRatingRequestDto createRatingRequestDto) {
        this.ratingService.createRating(createRatingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("Rating created successfully")
                .build());
    }
    //@formatter:on

    @GetMapping("/ratings")
    public ResponseEntity<ApiSuccessResponse<List<Rating>>> getRatingsByBookId(
            @ModelAttribute @Valid GetRatingsByBookIdRequestDto getRatingsByBookIdRequestDto) {
        Page<Rating> pagedRatings = this.ratingService.getRatingsByBookId(getRatingsByBookIdRequestDto);
        return ResponseEntity.ok(ApiSuccessResponse.<List<Rating>>builder()
                .status(HttpStatus.OK.value())
                .message("Ratings retrieved successfully")
                .data(pagedRatings.getContent())
                .meta(PagingAndSortingMetadata.builder()
                        .page(pagedRatings.getNumber())
                        .size(pagedRatings.getSize())
                        .last(pagedRatings.isLast())
                        .totalElements(pagedRatings.getTotalElements())
                        .build())
                .build());
    }
}

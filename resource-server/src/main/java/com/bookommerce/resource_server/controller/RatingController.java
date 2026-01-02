package com.bookommerce.resource_server.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.resource_server.dto.request.CreateRatingRequestDto;
import com.bookommerce.resource_server.dto.request.GetAllRatingsRequestDto;
import com.bookommerce.resource_server.dto.request.GetRatingsByBookIdRequestDto;
import com.bookommerce.resource_server.dto.request.Id;
import com.bookommerce.resource_server.dto.request.UpdateRatingRequestDto;
import com.bookommerce.resource_server.dto.response.ApiSuccessResponse;
import com.bookommerce.resource_server.dto.response.GetAllRatingsResponseDto;
import com.bookommerce.resource_server.dto.response.GetRatingsByBookIdResponseDto;
import com.bookommerce.resource_server.dto.response.PagingAndSortingMetadata;
import com.bookommerce.resource_server.service.RatingService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@RestController
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RatingController {

    RatingService ratingService;

    @GetMapping("/books/{bookId}/ratings")
    public ResponseEntity<ApiSuccessResponse<List<GetRatingsByBookIdResponseDto>>> getRatingsByBookId(
        @ModelAttribute @Valid GetRatingsByBookIdRequestDto getRatingsByBookIdRequestDto) {
        Page<GetRatingsByBookIdResponseDto> pagedRatings = this.ratingService.getRatingsByBookId(getRatingsByBookIdRequestDto);
        return ResponseEntity.ok(ApiSuccessResponse.<List<GetRatingsByBookIdResponseDto>>builder()
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

    @GetMapping("/ratings")
    public ResponseEntity<ApiSuccessResponse<List<GetAllRatingsResponseDto>>> getAllRatings(
        @ModelAttribute @Valid GetAllRatingsRequestDto getAllRatingsRequestDto) {
        Page<GetAllRatingsResponseDto> pagedRatings = this.ratingService.getAllRatings(getAllRatingsRequestDto);
        return ResponseEntity.ok(ApiSuccessResponse.<List<GetAllRatingsResponseDto>>builder()
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

    @PostMapping("/ratings")
    public ResponseEntity<ApiSuccessResponse<Void>> createRating(
        @RequestBody @Valid CreateRatingRequestDto createRatingRequestDto) {
        this.ratingService.createRating(createRatingRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("Rating submitted successfully and will be displayed if approved by admin")
                .build());
    }

    @PatchMapping("/ratings/{id}/approve")
    public ResponseEntity<ApiSuccessResponse<Void>> approveRatingById(@Valid Id id) {
        this.ratingService.approveRatingById(id.id());
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Rating approved successfully")
                .build());
    }
    
    @PutMapping("/ratings")
    public ResponseEntity<ApiSuccessResponse<Void>> updateRatingById(
        @RequestBody @Valid UpdateRatingRequestDto updateRatingRequestDto) {
        this.ratingService.updateRatingById(updateRatingRequestDto);
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Rating updated successfully")
                .build());
    }

    @PatchMapping("/ratings/{id}/reject")
    public ResponseEntity<ApiSuccessResponse<Void>> rejectRatingById(@Valid Id id) {
        this.ratingService.rejectRatingById(id.id());
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Rating rejected successfully")
                .build());
    }

    @DeleteMapping("/ratings/{id}")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteRatingById(@Valid Id id) {
        this.ratingService.deleteRatingById(id.id());
        return ResponseEntity.ok(ApiSuccessResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Rating deleted successfully")
                .build());
    }
}

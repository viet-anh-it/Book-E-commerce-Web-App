package com.bookommerce.resource_server.dto.request;

import org.springframework.data.domain.Sort.Direction;

import com.bookommerce.resource_server.constant.RatingsSortCriteria;
import com.bookommerce.resource_server.validation.validator.AllowedPageSizes;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// @formatter:off
public record GetRatingsByBookIdRequestDto(
    @Min(value = 0, message = "Book ID must be greater than or equal to 0")
    long bookId,

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    Integer page,

    
    @AllowedPageSizes(value = { 5, 10 })
    Integer size,

    RatingsSortCriteria sort,
    Direction order,

    @Min(value = 1, message = "Point must be greater than or equal to 1")
    @Max(value = 5, message = "Point must be less than or equal to 5")
    Integer point
) {
    public GetRatingsByBookIdRequestDto {
        if(page == null) page = 0;
        if(size == null) size = 5;
        if(sort == null) sort = RatingsSortCriteria.CREATED_AT;
        if(order == null) order = Direction.DESC;
    }
}

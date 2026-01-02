package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.resource_server.dto.request.CreateRatingRequestDto;
import com.bookommerce.resource_server.dto.response.GetAllRatingsResponseDto;
import com.bookommerce.resource_server.dto.response.GetRatingsByBookIdResponseDto;
import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.entity.Rating;
import com.bookommerce.resource_server.entity.Rating_;

// @formatter:off
@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = Rating_.ID, ignore = true)
    @Mapping(target = Rating_.CREATED_AT, ignore = true)
    @Mapping(target = Rating_.BOOK, ignore = true)
    @Mapping(target = Rating_.RATER, ignore = true)
    @Mapping(target = Rating_.APPROVED, ignore = true)
    Rating toRating(CreateRatingRequestDto createRatingRequestDto);

    GetRatingsByBookIdResponseDto toGetRatingsByBookIdResponseDto(Rating rating);

    GetAllRatingsResponseDto toGetAllRatingsResponseDto(
        GetAllRatingsResponseDto.Rating rating, GetAllRatingsResponseDto.Book book);

    GetAllRatingsResponseDto.Rating toGetAllRatingsResponseDto_Rating(Rating rating);

    GetAllRatingsResponseDto.Book toGetAllRatingsResponseDto_Book(Book book);
}

package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.resource_server.dto.request.CreateRatingRequestDto;
import com.bookommerce.resource_server.dto.response.GetAllRatingsResponseDto;
import com.bookommerce.resource_server.dto.response.GetRatingsByBookIdResponseDto;
import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.entity.Rating;

// @formatter:off
@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "rater", ignore = true)
    @Mapping(target = "approved", ignore = true)
    Rating toRating(CreateRatingRequestDto createRatingRequestDto);

    GetRatingsByBookIdResponseDto toGetRatingsByBookIdResponseDto(Rating rating);

    GetAllRatingsResponseDto toGetAllRatingsResponseDto(
        GetAllRatingsResponseDto.Rating rating, GetAllRatingsResponseDto.Book book);

    GetAllRatingsResponseDto.Rating toGetAllRatingsResponseDto_Rating(Rating rating);

    GetAllRatingsResponseDto.Book toGetAllRatingsResponseDto_Book(Book book);
}

package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.resource_server.dto.request.CreateRatingRequestDto;
import com.bookommerce.resource_server.entity.Rating;
import com.bookommerce.resource_server.entity.Rating_;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = Rating_.ID, ignore = true)
    @Mapping(target = Rating_.CREATED_AT, ignore = true)
    @Mapping(target = Rating_.BOOK, ignore = true)
    Rating toRating(CreateRatingRequestDto createRatingRequestDto);
}

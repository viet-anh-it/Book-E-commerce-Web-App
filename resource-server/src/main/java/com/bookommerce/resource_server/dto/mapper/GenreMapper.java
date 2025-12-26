package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.resource_server.dto.request.CreateGenreRequestDto;
import com.bookommerce.resource_server.entity.Genre;
import com.bookommerce.resource_server.entity.Genre_;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    @Mapping(target = Genre_.ID, ignore = true)
    @Mapping(target = Genre_.BOOKS, ignore = true)
    Genre toGenre(CreateGenreRequestDto createGenreRequestDto);
}

package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.resource_server.dto.request.CreateBookRequestDto;
import com.bookommerce.resource_server.dto.response.GetAllBooksResponseDto;
import com.bookommerce.resource_server.dto.response.GetBookByIdResponseDto;
import com.bookommerce.resource_server.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "genre", ignore = true)
    @Mapping(target = "ratingStatistic", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "thumbnailUrlPath", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    Book toBook(CreateBookRequestDto createBookRequestDto);

    @Mapping(target = "rating", source = "ratingStatistic.averagePoint")
    GetAllBooksResponseDto toGetAllBooksResponseDto(Book book);

    @Mapping(target = "ratings", source = "ratings")
    @Mapping(target = "genreId", source = "book.genre.id")
    GetBookByIdResponseDto toGetBookByIdResponseDto(Book book, GetBookByIdResponseDto.Ratings ratings);
}

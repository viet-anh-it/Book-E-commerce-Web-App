package com.bookommerce.resource_server.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookommerce.resource_server.dto.request.CreateBookRequestDto;
import com.bookommerce.resource_server.dto.response.GetAllBooksResponseDto;
import com.bookommerce.resource_server.dto.response.GetBookByIdResponseDto;
import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.entity.Book_;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = Book_.ID, ignore = true)
    @Mapping(target = Book_.GENRE, ignore = true)
    @Mapping(target = Book_.RATING_STATISTIC, ignore = true)
    @Mapping(target = Book_.RATINGS, ignore = true)
    @Mapping(target = Book_.THUMBNAIL_URL_PATH, ignore = true)
    Book toBook(CreateBookRequestDto createBookRequestDto);

    @Mapping(target = "rating", source = "ratingStatistic.averagePoint")
    GetAllBooksResponseDto toGetAllBooksResponseDto(Book book);

    @Mapping(target = "ratings", source = "ratings")
    @Mapping(target = "genreId", source = "book.genre.id")
    GetBookByIdResponseDto toGetBookByIdResponseDto(Book book, GetBookByIdResponseDto.Ratings ratings);
}

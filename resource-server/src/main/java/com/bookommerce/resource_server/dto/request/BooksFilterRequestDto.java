package com.bookommerce.resource_server.dto.request;

import org.springframework.data.domain.Sort.Direction;

import com.bookommerce.resource_server.constant.BooksSortCriteria;
import com.bookommerce.resource_server.validation.annotation.ValidPriceRange;
import com.bookommerce.resource_server.validation.validator.AllowedPageSizes;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidPriceRange
public class BooksFilterRequestDto {

    @Min(value = 0, message = "Min price must be greater than or equal to 0")
    @Max(value = 1_000_000, message = "Min price must be less than or equal to 1000000")
    double minPrice;

    @Min(value = 0, message = "Max price must be greater than or equal to 0")
    @Max(value = 1_000_000, message = "Max price must be less than or equal to 1000000")
    double maxPrice = 1_000_000;

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    int page;

    @AllowedPageSizes(value = { 10, 20 })
    int size = 10;

    BooksSortCriteria sort = BooksSortCriteria.TITLE;
    Direction order = Direction.ASC;
    String search;

    List<@Min(value = 0, message = "Genre id must be greater than or equal to 0") Integer> genres;

    @Min(value = 1, message = "Rating must be greater than or equal to 1")
    @Max(value = 5, message = "Rating must be less than or equal to 5")
    Integer rating;
}

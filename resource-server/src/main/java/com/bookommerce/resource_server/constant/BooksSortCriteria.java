package com.bookommerce.resource_server.constant;

import com.bookommerce.resource_server.entity.Book_;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum BooksSortCriteria {
    TITLE(Book_.TITLE),
    PRICE(Book_.PRICE),
    RATING("ratingStatistic.averagePoint");

    String property;
}

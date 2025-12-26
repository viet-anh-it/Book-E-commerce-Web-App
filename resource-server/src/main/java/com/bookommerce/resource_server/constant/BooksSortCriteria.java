package com.bookommerce.resource_server.constant;

import com.bookommerce.resource_server.entity.Book_;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Enum representing the available sorting criteria for books.
 * <p>
 * This enum defines the properties by which books can be sorted, such as title,
 * price, and rating.
 * It maps the enum constant to the actual property name used in the data layer.
 * </p>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum BooksSortCriteria {
    /**
     * Sort by book title.
     */
    TITLE(Book_.TITLE),

    /**
     * Sort by book price.
     */
    PRICE(Book_.PRICE),

    /**
     * Sort by average rating.
     */
    RATING("ratingStatistic.averagePoint");

    /**
     * The property name associated with the sort criteria.
     */
    String property;
}

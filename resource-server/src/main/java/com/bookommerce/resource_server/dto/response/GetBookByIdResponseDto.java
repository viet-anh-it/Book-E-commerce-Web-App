package com.bookommerce.resource_server.dto.response;

import java.time.Instant;
import java.util.List;

import com.bookommerce.resource_server.entity.RatingStatistic_;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

// @formatter:off
/**
 * DTO for single book response.
 * <p>
 * This record contains the detailed information of a book.
 * </p>
 *
 * @param id              the unique identifier of the book.
 * @param title           the title of the book.
 * @param author          the author of the book.
 * @param thumbnailUrlPath    the URL of the book's thumbnail.
 * @param price           the price of the book.
 * @param description     the description of the book.
 * @param ratingStatistic the rating statistic of the book.
 */
public record GetBookByIdResponseDto(
    long id,
    String title,
    String author,
    String thumbnailUrlPath,
    double price,
    String description,
    Ratings ratings,
    RatingStatistic ratingStatistic
) {
    /**
     * DTO for rating statistic.
     *
     * @param averagePoint the average rating point.
     * @param ratingCount  the total number of ratings.
     * @param _1PointCount the count of 1-star ratings.
     * @param _2PointCount the count of 2-star ratings.
     * @param _3PointCount the count of 3-star ratings.
     * @param _4PointCount the count of 4-star ratings.
     * @param _5PointCount the count of 5-star ratings.
     */
    public record RatingStatistic(
        double averagePoint,
        long ratingCount,

        @JsonProperty(value = RatingStatistic_._1_POINT_COUNT)
        int _1PointCount,

        @JsonProperty(value = RatingStatistic_._2_POINT_COUNT)
        int _2PointCount,

        @JsonProperty(value = RatingStatistic_._3_POINT_COUNT)
        int _3PointCount,

        @JsonProperty(value = RatingStatistic_._4_POINT_COUNT)
        int _4PointCount,

        @JsonProperty(value = RatingStatistic_._5_POINT_COUNT)
        int _5PointCount
    ) {}

    @Builder
    public record Ratings (
        List<Rating> data,
        PagingSortingMeta meta
    ) {
        @Builder
        public record Rating (
            String rater,
            int point,
            String comment,
            Instant createdAt
        ) {}

        @Builder
        public record PagingSortingMeta (
            int page,
            int size,
            boolean last,
            long total
        ) {}
    }
}

package com.bookommerce.resource_server.dto.response;

import java.time.Instant;
import java.util.List;

import com.bookommerce.resource_server.entity.RatingStatistic_;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

// @formatter:off
public record GetBookByIdResponseDto(
    long id,
    String title,
    String author,
    String thumbnailUrlPath,
    double price,
    int stock,
    long genreId,
    String description,
    Ratings ratings,
    RatingStatistic ratingStatistic
) {
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
            long id,
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

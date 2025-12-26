package com.bookommerce.resource_server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Entity representing rating statistics for a book.
 * <p>
 * This class maps to the "rating_statistics" table and stores aggregated rating
 * data.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "rating_statistics")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RatingStatistic {

    /**
     * Unique identifier for the statistic record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    /**
     * Average rating score.
     */
    double averagePoint;

    /**
     * Total number of ratings.
     */
    int ratingCount;

    /**
     * Count of 1-star ratings.
     */
    int _1PointCount;

    /**
     * Count of 2-star ratings.
     */
    int _2PointCount;

    /**
     * Count of 3-star ratings.
     */
    int _3PointCount;

    /**
     * Count of 4-star ratings.
     */
    int _4PointCount;

    /**
     * Count of 5-star ratings.
     */
    int _5PointCount;

    /**
     * The book associated with these statistics.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @JsonIgnore
    Book book;
}

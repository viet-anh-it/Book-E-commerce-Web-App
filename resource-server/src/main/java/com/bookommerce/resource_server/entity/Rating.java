package com.bookommerce.resource_server.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Entity representing a user rating for a book.
 * <p>
 * This class maps to the "ratings" table in the database.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "ratings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    /**
     * Unique identifier for the rating.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    /**
     * Name of the person who submitted the rating.
     */
    String rater;

    /**
     * Rating score (1-5).
     */
    int point;

    /**
     * Comment associated with the rating.
     */
    @Column(columnDefinition = "TEXT")
    String comment;

    /**
     * Timestamp when the rating was created.
     */
    @Column(name = "created_at")
    Instant createdAt;

    /**
     * The book being rated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @JsonIgnore
    Book book;
}

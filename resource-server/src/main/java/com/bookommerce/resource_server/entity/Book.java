package com.bookommerce.resource_server.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "books")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String title;
    String author;
    String thumbnailUrlPath;
    double price;
    int stock;

    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    @JsonIgnore
    Genre genre;

    @OneToMany(mappedBy = Rating_.BOOK, fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Rating> ratings;

    @OneToOne(mappedBy = RatingStatistic_.BOOK, fetch = FetchType.LAZY)
    @JsonIgnore
    RatingStatistic ratingStatistic;
}

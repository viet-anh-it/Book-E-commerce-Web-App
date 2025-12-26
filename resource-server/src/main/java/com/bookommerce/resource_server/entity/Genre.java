package com.bookommerce.resource_server.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Entity representing a book genre.
 * <p>
 * This class maps to the "genres" table in the database.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "genres")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {
    /**
     * Unique identifier for the genre.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    /**
     * Name of the genre.
     */
    String name;

    /**
     * Description of the genre.
     */
    @Column(columnDefinition = "MEDIUMTEXT")
    String description;

    /**
     * Set of books belonging to this genre.
     */
    @OneToMany(mappedBy = Book_.GENRE, fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Book> books;
}

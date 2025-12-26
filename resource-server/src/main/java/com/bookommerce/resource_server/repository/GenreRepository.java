package com.bookommerce.resource_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookommerce.resource_server.entity.Genre;

/**
 * Repository interface for {@link Genre} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations for
 * genres.
 * </p>
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Checks if a genre with the given name exists.
     *
     * @param name the name of the genre.
     * @return true if a genre with the given name exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Retrieves a genre by its name.
     *
     * @param name the name of the genre.
     * @return an {@link Optional} containing the genre if found, or empty
     *         otherwise.
     */
    Optional<Genre> findByName(String name);
}

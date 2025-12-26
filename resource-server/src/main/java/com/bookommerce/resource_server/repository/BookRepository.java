package com.bookommerce.resource_server.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bookommerce.resource_server.entity.Book;

/**
 * Repository interface for {@link Book} entities.
 * <p>
 * This interface extends {@link JpaRepository} and
 * {@link JpaSpecificationExecutor} to provide
 * CRUD operations and dynamic query execution capabilities for books.
 * </p>
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    /**
     * Retrieves a page of books matching the given specification.
     * <p>
     * This method uses an entity graph to eagerly fetch the associated
     * {@link com.bookommerce.resource_server.entity.RatingStatistic}.
     * </p>
     *
     * @param spec     the specification to filter books.
     * @param pageable the pagination information.
     * @return a {@link Page} of books matching the specification.
     */
    @EntityGraph(attributePaths = { "ratingStatistic" })
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    /**
     * Retrieves a book by its ID.
     * <p>
     * This method uses an entity graph to eagerly fetch the associated
     * {@link com.bookommerce.resource_server.entity.RatingStatistic}.
     * </p>
     *
     * @param id the ID of the book.
     * @return an {@link Optional} containing the book if found, or empty otherwise.
     */
    @EntityGraph(attributePaths = { "ratingStatistic" })
    Optional<Book> findById(long id);
}

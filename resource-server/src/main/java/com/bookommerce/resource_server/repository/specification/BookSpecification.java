package com.bookommerce.resource_server.repository.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.entity.Book_;
import com.bookommerce.resource_server.entity.Genre_;
import com.bookommerce.resource_server.entity.RatingStatistic_;

/**
 * Specifications for querying {@link Book} entities.
 * <p>
 * This class provides static methods to create {@link Specification} instances
 * for filtering books
 * based on various criteria such as price, title, author, genre, and rating.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookSpecification {

    /**
     * Creates a specification to filter books by price range.
     *
     * @param minPrice the minimum price.
     * @param maxPrice the maximum price.
     * @return a {@link Specification} for the price range.
     */
    public static Specification<Book> priceBetween(double minPrice, double maxPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Book_.PRICE), minPrice, maxPrice);
    }

    /**
     * Creates a specification to filter books by title (case-insensitive partial
     * match).
     *
     * @param title the title keyword to search for.
     * @return a {@link Specification} for the title search.
     */
    public static Specification<Book> titleLike(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Book_.TITLE), "%" + title + "%");
    }

    /**
     * Creates a specification to filter books by author (case-insensitive partial
     * match).
     *
     * @param author the author keyword to search for.
     * @return a {@link Specification} for the author search.
     */
    public static Specification<Book> authorLike(String author) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Book_.AUTHOR), "%" + author + "%");
    }

    /**
     * Creates a specification to filter books by a list of genre IDs.
     *
     * @param genres the list of genre IDs.
     * @return a {@link Specification} for the genre filter.
     */
    public static Specification<Book> genreIn(List<Integer> genres) {
        return (root, query, criteriaBuilder) -> root.get(Book_.GENRE).get(Genre_.ID).in(genres);
    }

    /**
     * Creates a specification to filter books by minimum average rating.
     *
     * @param rating the minimum average rating.
     * @return a {@link Specification} for the rating filter.
     */
    public static Specification<Book> ratingGreaterThanOrEqualTo(int rating) {
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .greaterThanOrEqualTo(root.get(Book_.RATING_STATISTIC).get(RatingStatistic_.AVERAGE_POINT), rating);
    }
}

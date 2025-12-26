package com.bookommerce.resource_server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookommerce.resource_server.entity.Rating;

/**
 * Repository interface for {@link Rating} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations for
 * ratings.
 * </p>
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Page<Rating> findByBook_Id(long bookId, Pageable pageable);

    Page<Rating> findByBook_IdAndPoint(long bookId, int point, Pageable pageable);
}

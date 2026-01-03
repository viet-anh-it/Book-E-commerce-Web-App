package com.bookommerce.resource_server.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.bookommerce.resource_server.entity.Rating;
import com.bookommerce.resource_server.entity.Rating_;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @EntityGraph(attributePaths = { Rating_.BOOK })
    @NonNull
    Page<Rating> findAll(@NonNull Pageable pageable);

    @EntityGraph(attributePaths = { Rating_.BOOK })
    Page<Rating> findByPoint(int point, Pageable pageable);

    Page<Rating> findByApprovedAndBook_Id(boolean approved, long bookId, Pageable pageable);

    Page<Rating> findByApprovedAndBook_IdAndPoint(boolean approved, long bookId, int point, Pageable pageable);

    Optional<Rating> findByRaterAndBook_Id(String rater, long bookId);
}

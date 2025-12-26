package com.bookommerce.resource_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookommerce.resource_server.entity.RatingStatistic;

/**
 * Repository interface for {@link RatingStatistic} entities.
 * <p>
 * This interface extends {@link JpaRepository} to provide CRUD operations for
 * rating statistics.
 * </p>
 */
@Repository
public interface RatingStatisticRepository extends JpaRepository<RatingStatistic, Long> {

}

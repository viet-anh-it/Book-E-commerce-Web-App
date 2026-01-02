package com.bookommerce.resource_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookommerce.resource_server.entity.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    boolean existsByName(String name);

    Optional<Genre> findByName(String name);
}

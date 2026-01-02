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
import com.bookommerce.resource_server.entity.Book_;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @EntityGraph(attributePaths = { Book_.RATING_STATISTIC })
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);

    @EntityGraph(attributePaths = { Book_.RATING_STATISTIC })
    Optional<Book> findById(long id);
}

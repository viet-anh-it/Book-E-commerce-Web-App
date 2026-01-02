package com.bookommerce.resource_server.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.repository.BookRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookCrudService {

    BookRepository bookRepository;

    @PreAuthorize("hasRole('ROLE_PRODUCT_MANAGER')")
    public Book save(Book book) {
        return this.bookRepository.save(book);
    }

    @PreAuthorize("hasRole('ROLE_PRODUCT_MANAGER')")
    public void delete(Book book) {
        this.bookRepository.delete(book);
    }

    public Page<Book> findAll(Specification<Book> spec, Pageable pageable) {
        return this.bookRepository.findAll(spec, pageable);
    }

    public Optional<Book> findById(long id) {
        return this.bookRepository.findById(id);
    }
}

package com.bookommerce.resource_server.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookommerce.resource_server.dto.mapper.RatingMapper;
import com.bookommerce.resource_server.dto.request.CreateRatingRequestDto;
import com.bookommerce.resource_server.dto.request.GetRatingsByBookIdRequestDto;
import com.bookommerce.resource_server.entity.Rating;
import com.bookommerce.resource_server.entity.Rating_;
import com.bookommerce.resource_server.repository.BookRepository;
import com.bookommerce.resource_server.repository.RatingRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {

    RatingRepository ratingRepository;
    RatingMapper ratingMapper;
    BookRepository bookRepository;

    // @formatter:off
    @Transactional
    public void createRating(CreateRatingRequestDto createRatingRequestDto) {
        Rating rating = this.ratingMapper.toRating(createRatingRequestDto);
        rating.setBook(this.bookRepository.findById(createRatingRequestDto.bookId())
                .orElseThrow(() -> new RuntimeException("Book not found")));
        rating.setCreatedAt(Instant.now());
        this.ratingRepository.save(rating);
    }
    // @formatter:on

    public Page<Rating> get5NewestRatingsByBookId(long bookId) {
        this.bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        int page = 0;
        int size = 5;
        Direction direction = Direction.DESC;
        String property = Rating_.CREATED_AT;
        Pageable pageable = PageRequest.of(page, size, direction, property);

        return this.ratingRepository.findByBook_Id(bookId, pageable);
    }

    public Page<Rating> getRatingsByBookId(GetRatingsByBookIdRequestDto getRatingsByBookIdRequestDto) {
        this.bookRepository.findById(getRatingsByBookIdRequestDto.bookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        int page = getRatingsByBookIdRequestDto.page();
        int size = getRatingsByBookIdRequestDto.size();
        Direction direction = getRatingsByBookIdRequestDto.order();
        String property = getRatingsByBookIdRequestDto.sort().getProperty();
        Pageable pageable = PageRequest.of(page, size, direction, property);

        // @formatter:off
        return getRatingsByBookIdRequestDto.point() == null
                ? this.ratingRepository.findByBook_Id(getRatingsByBookIdRequestDto.bookId(), pageable)
                : this.ratingRepository.findByBook_IdAndPoint(getRatingsByBookIdRequestDto.bookId(), getRatingsByBookIdRequestDto.point(), pageable);
    }
}

package com.bookommerce.resource_server.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.bookommerce.resource_server.dto.mapper.RatingMapper;
import com.bookommerce.resource_server.dto.request.CreateRatingRequestDto;
import com.bookommerce.resource_server.dto.request.GetAllRatingsRequestDto;
import com.bookommerce.resource_server.dto.request.GetRatingsByBookIdRequestDto;
import com.bookommerce.resource_server.dto.request.UpdateRatingRequestDto;
import com.bookommerce.resource_server.dto.response.GetAllRatingsResponseDto;
import com.bookommerce.resource_server.dto.response.GetRatingsByBookIdResponseDto;
import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.entity.Rating;
import com.bookommerce.resource_server.entity.RatingStatistic;
import com.bookommerce.resource_server.entity.Rating_;
import com.bookommerce.resource_server.exception.AccessDeniedException;
import com.bookommerce.resource_server.exception.BookNotFoundException;
import com.bookommerce.resource_server.exception.DuplicateRatingException;
import com.bookommerce.resource_server.exception.IllegalResourceStateException;
import com.bookommerce.resource_server.exception.ResourceNotFoundException;
import com.bookommerce.resource_server.repository.BookRepository;
import com.bookommerce.resource_server.repository.RatingRepository;
import com.bookommerce.resource_server.repository.RatingStatisticRepository;
import com.bookommerce.resource_server.utils.ValidationUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

// @formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {

    RatingRepository ratingRepository;
    RatingMapper ratingMapper;
    BookRepository bookRepository;
    RatingStatisticRepository ratingStatisticRepository;

    @Transactional
    public void createRating(CreateRatingRequestDto createRatingRequestDto) {
        Rating rating = this.ratingMapper.toRating(createRatingRequestDto);

        long bookId = createRatingRequestDto.bookId();
        Optional<Book> optionalBook = this.bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(
                    createRatingRequestDto,
                    "createRatingRequestDto",
                    null,
                    "Book not found with ID: " + bookId);
                throw new BookNotFoundException(bindingResult);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Rating> optionalRating = this.ratingRepository.findByRaterAndBook_Id(username, bookId);
        if (optionalRating.isPresent()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(
                    createRatingRequestDto,
                    "createRatingRequestDto",
                    null,
                    "You have given a rating for this book already");
            throw new DuplicateRatingException(bindingResult);
        }

        Book book = optionalBook.get();
        rating.setBook(book);
        rating.setRater(username);
        rating.setCreatedAt(Instant.now());
        rating.setApproved(false);
        this.ratingRepository.save(rating);
    }

    public Page<Rating> get5NewestRatingsByBookId(long bookId) {
        this.bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        int page = 0;
        int size = 5;
        Direction direction = Direction.DESC;
        String property = Rating_.CREATED_AT;
        Pageable pageable = PageRequest.of(page, size, direction, property);

        boolean approved = true;
        return this.ratingRepository.findByApprovedAndBook_Id(approved, bookId, pageable);
    }

    public Page<GetRatingsByBookIdResponseDto> getRatingsByBookId(GetRatingsByBookIdRequestDto getRatingsByBookIdRequestDto) {
        long bookId = getRatingsByBookIdRequestDto.bookId();
        Optional<Book> optionalBook = this.bookRepository.findById(bookId);
        if (optionalBook.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(
                    getRatingsByBookIdRequestDto,
                    "getRatingsByBookIdRequestDto",
                    null,
                    "Book not found with ID: " + bookId);
            throw new BookNotFoundException(bindingResult);
        }

        int page = getRatingsByBookIdRequestDto.page();
        int size = getRatingsByBookIdRequestDto.size();
        Direction direction = getRatingsByBookIdRequestDto.order();
        String property = getRatingsByBookIdRequestDto.sort().getProperty();
        Pageable pageable = PageRequest.of(page, size, direction, property);

        boolean approved = true;
        Integer ratingPoint = getRatingsByBookIdRequestDto.point();
        Page<Rating> pagedRatings = ratingPoint == null
                ? this.ratingRepository.findByApprovedAndBook_Id(approved, bookId, pageable)
                : this.ratingRepository.findByApprovedAndBook_IdAndPoint(approved, bookId, ratingPoint, pageable);
        return pagedRatings.map(rating -> this.ratingMapper.toGetRatingsByBookIdResponseDto(rating));
    }

    public Page<GetAllRatingsResponseDto> getAllRatings(GetAllRatingsRequestDto getAllRatingsRequestDto) {
        int page = getAllRatingsRequestDto.page();
        int size = getAllRatingsRequestDto.size();
        Direction direction = getAllRatingsRequestDto.order();
        String property = getAllRatingsRequestDto.sort().getProperty();
        Integer ratingPoint = getAllRatingsRequestDto.point();

        Pageable pageable = PageRequest.of(page, size, direction, property);
        Page<Rating> pagedRatings = ( ratingPoint == null )
                ? this.ratingRepository.findAll(pageable)
                : this.ratingRepository.findByPoint(ratingPoint, pageable);
        return pagedRatings.map(rating -> {
            GetAllRatingsResponseDto.Rating ratingResponseDto = this.ratingMapper.toGetAllRatingsResponseDto_Rating(rating);
            GetAllRatingsResponseDto.Book bookResponseDto = this.ratingMapper.toGetAllRatingsResponseDto_Book(rating.getBook());
            return this.ratingMapper.toGetAllRatingsResponseDto(ratingResponseDto, bookResponseDto);
        });
    }

    @Transactional
    public void approveRatingById(long id) {
        Optional<Rating> optionalRating = this.ratingRepository.findById(id);
        if (optionalRating.isEmpty()) {
            BindingResult bindingResult = ValidationUtils.createBindingResult(id, "id", null, "Rating with ID: [" + id + "] is not found");
            throw new ResourceNotFoundException(bindingResult);
        }

        Rating rating = optionalRating.get();
        if (rating.isApproved()) {
            BindingResult bindingResult = ValidationUtils.createBindingResult(id, "id", null, "Rating with ID: [" + id + "] has been approved before");
            throw new IllegalResourceStateException(bindingResult);
        }

        rating.setApproved(true);
        this.ratingRepository.save(rating);
        RatingStatistic ratingStatistic = rating.getBook().getRatingStatistic();
        switch (rating.getPoint()) {
            case 1:
                ratingStatistic.set_1PointCount(ratingStatistic.get_1PointCount() + 1);
                break;
            case 2:
                ratingStatistic.set_2PointCount(ratingStatistic.get_2PointCount() + 1);
                break;
            case 3:
                ratingStatistic.set_3PointCount(ratingStatistic.get_3PointCount() + 1);
                break;
            case 4:
                ratingStatistic.set_4PointCount(ratingStatistic.get_4PointCount() + 1);
                break;
            case 5:
                ratingStatistic.set_5PointCount(ratingStatistic.get_5PointCount() + 1);
                break;
            default:
                break;
        }
        ratingStatistic.setRatingCount(ratingStatistic.getRatingCount() + 1);
        int totalPoint = 
            1*ratingStatistic.get_1PointCount() + 
            2*ratingStatistic.get_2PointCount() + 
            3*ratingStatistic.get_3PointCount() + 
            4*ratingStatistic.get_4PointCount() + 
            5*ratingStatistic.get_5PointCount();
        ratingStatistic.setAveragePoint((double) totalPoint / ratingStatistic.getRatingCount());
        this.ratingStatisticRepository.save(ratingStatistic);
    }

    @Transactional
    public void rejectRatingById(long id) {
        Optional<Rating> optionalRating = this.ratingRepository.findById(id);
        if (optionalRating.isEmpty()) {
            BindingResult bindingResult = ValidationUtils.createBindingResult(id, "id", null, "Rating not found with ID: " + id);
            throw new ResourceNotFoundException(bindingResult);
        }

        Rating rating = optionalRating.get();
        if (!rating.isApproved()) {
            BindingResult bindingResult = ValidationUtils.createBindingResult(id, "id", null, "Rating with ID: [" + id + "] has been rejected before");
            throw new IllegalResourceStateException(bindingResult);
        }
        rating.setApproved(false);
        this.ratingRepository.save(rating);

        RatingStatistic ratingStatistic = rating.getBook().getRatingStatistic();
        switch (rating.getPoint()) {
            case 1: ratingStatistic.set_1PointCount(ratingStatistic.get_1PointCount() - 1); break;
            case 2: ratingStatistic.set_2PointCount(ratingStatistic.get_2PointCount() - 1); break;
            case 3: ratingStatistic.set_3PointCount(ratingStatistic.get_3PointCount() - 1); break;
            case 4: ratingStatistic.set_4PointCount(ratingStatistic.get_4PointCount() - 1); break;
            case 5: ratingStatistic.set_5PointCount(ratingStatistic.get_5PointCount() - 1); break;
            default: break;
        }
        ratingStatistic.setRatingCount(ratingStatistic.getRatingCount() - 1);
        int totalPoint = 
            1*ratingStatistic.get_1PointCount() + 
            2*ratingStatistic.get_2PointCount() + 
            3*ratingStatistic.get_3PointCount() + 
            4*ratingStatistic.get_4PointCount() + 
            5*ratingStatistic.get_5PointCount();
        double averagePoint = (double) totalPoint / ratingStatistic.getRatingCount();
        if (Double.isNaN(averagePoint)) {
            averagePoint = 0;
        }
        ratingStatistic.setAveragePoint(averagePoint);
        this.ratingStatisticRepository.save(ratingStatistic);
    }

    @Transactional
    public void deleteRatingById(long id) {
        Optional<Rating> optionalRating = this.ratingRepository.findById(id);
        if (optionalRating.isEmpty()) {
            BindingResult bindingResult = ValidationUtils.createBindingResult(id, "id", null, "Rating not found with ID: " + id);
            throw new ResourceNotFoundException(bindingResult);
        }

        Rating rating = optionalRating.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentAuthenticatedUsername = authentication.getName();
        boolean isOwner = currentAuthenticatedUsername.equals(rating.getRater());
        Set<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());
        boolean isReviewManager = authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_REVIEW_MANAGER"));
        if (!isOwner && !isReviewManager) {
            throw new AccessDeniedException();
        }

        if (rating.isApproved()) {
            RatingStatistic ratingStatistic = rating.getBook().getRatingStatistic();
            switch (rating.getPoint()) {
                case 1: ratingStatistic.set_1PointCount(ratingStatistic.get_1PointCount() - 1); break;
                case 2: ratingStatistic.set_2PointCount(ratingStatistic.get_2PointCount() - 1); break;
                case 3: ratingStatistic.set_3PointCount(ratingStatistic.get_3PointCount() - 1); break;
                case 4: ratingStatistic.set_4PointCount(ratingStatistic.get_4PointCount() - 1); break;
                case 5: ratingStatistic.set_5PointCount(ratingStatistic.get_5PointCount() - 1); break;
                default: break;
            }
            ratingStatistic.setRatingCount(ratingStatistic.getRatingCount() - 1);
            int totalPoint = 
                1*ratingStatistic.get_1PointCount() + 
                2*ratingStatistic.get_2PointCount() + 
                3*ratingStatistic.get_3PointCount() + 
                4*ratingStatistic.get_4PointCount() + 
                5*ratingStatistic.get_5PointCount();
            double averagePoint = (double) totalPoint / ratingStatistic.getRatingCount();
            if (Double.isNaN(averagePoint)) {
                averagePoint = 0;
            }
            ratingStatistic.setAveragePoint(averagePoint);
            this.ratingStatisticRepository.save(ratingStatistic);
        }
        this.ratingRepository.deleteById(id);
    }

    @Transactional
    public void updateRatingById(UpdateRatingRequestDto updateRatingRequestDto) {
        long id = updateRatingRequestDto.id();
        Optional<Rating> optionalRating = this.ratingRepository.findById(id);
        if (optionalRating.isEmpty()) {
            BindingResult bindingResult = ValidationUtils.createBindingResult(id, "id", null, "Rating with ID: [" + id + "] is not found");
            throw new ResourceNotFoundException(bindingResult);
        }

        Rating rating = optionalRating.get();
        String currentAuthenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!rating.getRater().equals(currentAuthenticatedUsername)) {
            throw new AccessDeniedException();
        }

        if (rating.isApproved()) {
            RatingStatistic ratingStatistic = rating.getBook().getRatingStatistic();
            switch (rating.getPoint()) {
                case 1: ratingStatistic.set_1PointCount(ratingStatistic.get_1PointCount() - 1); break;
                case 2: ratingStatistic.set_2PointCount(ratingStatistic.get_2PointCount() - 1); break;
                case 3: ratingStatistic.set_3PointCount(ratingStatistic.get_3PointCount() - 1); break;
                case 4: ratingStatistic.set_4PointCount(ratingStatistic.get_4PointCount() - 1); break;
                case 5: ratingStatistic.set_5PointCount(ratingStatistic.get_5PointCount() - 1); break;
                default: break;
            }
            ratingStatistic.setRatingCount(ratingStatistic.getRatingCount() - 1);
            int totalPoint = 
                1*ratingStatistic.get_1PointCount() + 
                2*ratingStatistic.get_2PointCount() + 
                3*ratingStatistic.get_3PointCount() + 
                4*ratingStatistic.get_4PointCount() + 
                5*ratingStatistic.get_5PointCount();
            double averagePoint = (double) totalPoint / ratingStatistic.getRatingCount();
            if (Double.isNaN(averagePoint)) {
                averagePoint = 0;
            }
            ratingStatistic.setAveragePoint(averagePoint);
            this.ratingStatisticRepository.save(ratingStatistic);
        }

        rating.setPoint(updateRatingRequestDto.point());
        rating.setComment(updateRatingRequestDto.comment());
        rating.setApproved(false);
        this.ratingRepository.save(rating);
    }
}

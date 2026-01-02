package com.bookommerce.resource_server.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.bookommerce.resource_server.dto.mapper.BookMapper;
import com.bookommerce.resource_server.dto.request.BookIdRequestDto;
import com.bookommerce.resource_server.dto.request.BooksFilterRequestDto;
import com.bookommerce.resource_server.dto.request.CreateBookRequestDto;
import com.bookommerce.resource_server.dto.request.UpdateBookByIdRequestDto;
import com.bookommerce.resource_server.dto.response.GetAllBooksResponseDto;
import com.bookommerce.resource_server.dto.response.GetBookByIdResponseDto;
import com.bookommerce.resource_server.entity.Book;
import com.bookommerce.resource_server.entity.Cart;
import com.bookommerce.resource_server.entity.Genre;
import com.bookommerce.resource_server.entity.Rating;
import com.bookommerce.resource_server.entity.RatingStatistic;
import com.bookommerce.resource_server.exception.BookNotFoundException;
import com.bookommerce.resource_server.exception.GenreNotFoundException;
import com.bookommerce.resource_server.repository.BookRepository;
import com.bookommerce.resource_server.repository.CartRepository;
import com.bookommerce.resource_server.repository.GenreRepository;
import com.bookommerce.resource_server.repository.RatingStatisticRepository;
import com.bookommerce.resource_server.repository.specification.BookSpecification;
import com.bookommerce.resource_server.utils.ValidationUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

//@formatter:off
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {

    @NonFinal
    @Value(value = "${upload.image.book}")
    String BOOK_IMAGE_UPLOAD_DIR;

    BookCrudService bookCrudService;
    BookRepository bookRepository;
    RatingStatisticRepository ratingStatisticRepository;
    BookMapper bookMapper;
    RatingService ratingService;
    GenreRepository genreRepository;
    CartRepository cartRepository;

    @Transactional
    public void createBook(CreateBookRequestDto createBookRequestDto) throws IOException {
        Book book = this.bookMapper.toBook(createBookRequestDto);
        long genreId = createBookRequestDto.getGenreId();

        Optional<Genre> optionalGenre = this.genreRepository.findById(genreId);
        if (optionalGenre.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(createBookRequestDto, "createBookRequestDto", "genreId", "Genre not found with id: " + genreId);
            throw new GenreNotFoundException(bindingResult);
        }

        MultipartFile bookImage = createBookRequestDto.getImage();
        String cleanPath = StringUtils.cleanPath(bookImage.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(cleanPath);
        String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path destination = Paths.get(BOOK_IMAGE_UPLOAD_DIR, uniqueFileName);
        bookImage.transferTo(destination);

        book.setGenre(optionalGenre.get());
        book.setThumbnailUrlPath("/images/books/" + uniqueFileName);
        try {
            this.bookCrudService.save(book);
        } catch(Exception exception) {
            Files.delete(destination);
            throw exception;
        }
        RatingStatistic ratingStatistic = new RatingStatistic();
        ratingStatistic.setBook(book);
        this.ratingStatisticRepository.save(ratingStatistic);
    }

    public Page<GetAllBooksResponseDto> getAllBooks(BooksFilterRequestDto booksFilterRequestDto) {
        int pageNumber = booksFilterRequestDto.getPage();
        int pageSize = booksFilterRequestDto.getSize();
        Direction sortDirection = booksFilterRequestDto.getOrder();
        String sortBy = booksFilterRequestDto.getSort().getProperty();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortDirection, sortBy);

        double minPrice = booksFilterRequestDto.getMinPrice();
        double maxPrice = booksFilterRequestDto.getMaxPrice();
        Specification<Book> priceBetween = BookSpecification.priceBetween(minPrice, maxPrice);

        Specification<Book> combinedSpecification = Specification.where(priceBetween);

        String search = booksFilterRequestDto.getSearch();
        if (search != null) {
            Specification<Book> titleLike = BookSpecification.titleLike(search);
            Specification<Book> authorLike = BookSpecification.authorLike(search);
            Specification<Book> titleLikeOrAuthorLike = titleLike.or(authorLike);
            combinedSpecification = combinedSpecification.and(titleLikeOrAuthorLike);
        }

        List<Integer> genres = booksFilterRequestDto.getGenres();
        if (genres != null) {
            Specification<Book> genreIn = BookSpecification.genreIn(genres);
            combinedSpecification = combinedSpecification.and(genreIn);
        }

        Integer rating = booksFilterRequestDto.getRating();
        if (rating != null) {
            Specification<Book> ratingGreaterThanOrEqualTo = BookSpecification.ratingGreaterThanOrEqualTo(rating);
            combinedSpecification = combinedSpecification.and(ratingGreaterThanOrEqualTo);
        }

        Page<Book> pagedBooks = this.bookCrudService.findAll(combinedSpecification, pageable);
        return pagedBooks.map(book -> this.bookMapper.toGetAllBooksResponseDto(book));
    }

    public GetBookByIdResponseDto getBookById(BookIdRequestDto bookIdRequestDto) {
        Optional<Book> optionalBook = this.bookCrudService.findById(bookIdRequestDto.id());
        if (optionalBook.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(bookIdRequestDto, "bookIdRequestDto", "id", "Could not find a book with ID: " + bookIdRequestDto.id());
            throw new BookNotFoundException(bindingResult);
        }
        Book book = optionalBook.get();
        Page<Rating> pagedRatings = this.ratingService.get5NewestRatingsByBookId(bookIdRequestDto.id());
        GetBookByIdResponseDto.Ratings.PagingSortingMeta ratingsPagingSortingMeta = GetBookByIdResponseDto.Ratings.PagingSortingMeta.builder()
                .page(pagedRatings.getPageable().getPageNumber())
                .size(pagedRatings.getPageable().getPageSize())
                .last(pagedRatings.isLast())
                .total(pagedRatings.getTotalElements())
                .build();
        GetBookByIdResponseDto.Ratings ratings = GetBookByIdResponseDto.Ratings.builder()
                .data(pagedRatings.getContent().stream().map(rating -> GetBookByIdResponseDto.Ratings.Rating.builder()
                        .rater(rating.getRater())
                        .point(rating.getPoint())
                        .comment(rating.getComment())
                        .createdAt(rating.getCreatedAt())
                        .build())
                .toList())
                .meta(ratingsPagingSortingMeta)
                .build();
        return this.bookMapper.toGetBookByIdResponseDto(book, ratings);
    }

    @Transactional
    public void updateBook(BookIdRequestDto bookIdRequestDto, UpdateBookByIdRequestDto updateBookRequestDto) throws IOException {
        Optional<Book> optionalBook = this.bookCrudService.findById(bookIdRequestDto.id());
        if (optionalBook.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(bookIdRequestDto, "bookIdRequestDto", "id", "Could not find a book with ID: " + bookIdRequestDto.id());
            throw new BookNotFoundException(bindingResult);
        }

        long genreId = updateBookRequestDto.genreId();
        Optional<Genre> optionalGenre = this.genreRepository.findById(genreId);
        if (optionalGenre.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(updateBookRequestDto, "updateBookRequestDto", "genreId", "Genre not found with id: " + genreId);
            throw new GenreNotFoundException(bindingResult);
        }

        Book book = optionalBook.get();
        MultipartFile bookImageFile = updateBookRequestDto.image();
        Path destination = null;
        if (bookImageFile != null && !bookImageFile.isEmpty()) {
            Files.delete(Paths.get(BOOK_IMAGE_UPLOAD_DIR, StringUtils.getFilename(book.getThumbnailUrlPath())));
            String cleanPath = StringUtils.cleanPath(bookImageFile.getOriginalFilename());
            String fileExtension = StringUtils.getFilenameExtension(cleanPath);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;
            destination = Paths.get(BOOK_IMAGE_UPLOAD_DIR, uniqueFileName);
            bookImageFile.transferTo(destination);
            book.setThumbnailUrlPath("/images/books/" + uniqueFileName);
        }

        book.setGenre(optionalGenre.get());
        book.setTitle(updateBookRequestDto.title());
        book.setAuthor(updateBookRequestDto.author());
        book.setPrice(updateBookRequestDto.price());
        book.setStock(updateBookRequestDto.stock());
        book.setDescription(updateBookRequestDto.description());
        try {
            this.bookCrudService.save(book);
        } catch (Exception exception) {
            if (destination != null) {
                Files.delete(destination);
            }
            throw exception;
        }
    }

    @Transactional
    public void deleteBookById(BookIdRequestDto bookIdRequestDto) throws IOException {
        Optional<Book> optionalBook = this.bookCrudService.findById(bookIdRequestDto.id());
        if (optionalBook.isEmpty()) {
            BindingResult bindingResult = 
                ValidationUtils.createBindingResult(bookIdRequestDto, "bookIdRequestDto", null, "Could not find a book with ID: " + bookIdRequestDto.id());
            throw new BookNotFoundException(bindingResult);
        }

        Book book = optionalBook.get();
        book.getCartItems().forEach(cartItem -> {
            Cart cart = cartItem.getCart();
            cart.getCartItems().remove(cartItem);

            double newCartTotalPrice = cart.getTotalPrice() - cartItem.getSubtotal();
            cart.setTotalPrice(newCartTotalPrice);
            int newCartItemCount = cart.getItemCount() - cartItem.getQuantity();
            cart.setItemCount(newCartItemCount);

            this.cartRepository.save(cart);
        });
        Files.delete(Paths.get(BOOK_IMAGE_UPLOAD_DIR, StringUtils.getFilename(book.getThumbnailUrlPath())));
        this.bookCrudService.delete(book);
    }
}

package com.bookommerce.resource_server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookommerce.resource_server.dto.request.BookIdRequestDto;
import com.bookommerce.resource_server.dto.request.BooksFilterRequestDto;
import com.bookommerce.resource_server.dto.request.CreateBookRequestDto;
import com.bookommerce.resource_server.dto.request.UpdateBookByIdRequestDto;
import com.bookommerce.resource_server.dto.response.ApiSuccessResponse;
import com.bookommerce.resource_server.dto.response.GetAllBooksResponseDto;
import com.bookommerce.resource_server.dto.response.GetBookByIdResponseDto;
import com.bookommerce.resource_server.dto.response.PagingAndSortingMetadata;
import com.bookommerce.resource_server.service.BookService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

        BookService bookService;

        //@formatter:off
        @PostMapping(path = "/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ApiSuccessResponse<Void>> createBook(
                @ModelAttribute @Valid CreateBookRequestDto createBookRequestDto) throws IOException {
                this.bookService.createBook(createBookRequestDto);
                ApiSuccessResponse<Void> apiSuccessResponse = ApiSuccessResponse.<Void>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Book created successfully")
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(apiSuccessResponse);
        }

        // @formatter:off
        @GetMapping("/books")
        public ResponseEntity<ApiSuccessResponse<List<GetAllBooksResponseDto>>> getAllBooks(
                @ModelAttribute @Valid BooksFilterRequestDto booksFilterRequestDto) {
                Page<GetAllBooksResponseDto> books = this.bookService.getAllBooks(booksFilterRequestDto);
                ApiSuccessResponse<List<GetAllBooksResponseDto>> apiSuccessResponse = ApiSuccessResponse.<List<GetAllBooksResponseDto>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Books fetched successfully")
                                .data(books.getContent())
                                .meta(PagingAndSortingMetadata.builder()
                                                .page(books.getPageable().getPageNumber())
                                                .size(books.getPageable().getPageSize())
                                                .last(books.isLast())
                                                .totalElements(books.getTotalElements())
                                                .build())
                                .build();
                return ResponseEntity.status(HttpStatus.OK).body(apiSuccessResponse);
        }

        @GetMapping("/books/{id}")
        public ResponseEntity<ApiSuccessResponse<GetBookByIdResponseDto>> getBookById(@Valid BookIdRequestDto bookIdRequestDto) {
                GetBookByIdResponseDto book = this.bookService.getBookById(bookIdRequestDto);
                return ResponseEntity.status(HttpStatus.OK).body(ApiSuccessResponse.<GetBookByIdResponseDto>builder()
                                .status(HttpStatus.OK.value())
                                .message("Book fetched successfully")
                                .data(book)
                                .build());
        }

        @PutMapping("/books/{id}")
        public ResponseEntity<ApiSuccessResponse<Void>> updateBookById(
                @Valid BookIdRequestDto bookIdRequestDto,
                @RequestBody @Valid UpdateBookByIdRequestDto updateBookRequestDto) {
                this.bookService.updateBook(bookIdRequestDto, updateBookRequestDto);
                ApiSuccessResponse<Void> apiSuccessResponse = ApiSuccessResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Book updated successfully")
                                .build();
                return ResponseEntity.status(HttpStatus.OK).body(apiSuccessResponse);
        }
}

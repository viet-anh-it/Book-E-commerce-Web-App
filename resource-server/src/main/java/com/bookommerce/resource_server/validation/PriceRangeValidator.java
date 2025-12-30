package com.bookommerce.resource_server.validation;

import com.bookommerce.resource_server.dto.request.BooksFilterRequestDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceRangeValidator implements ConstraintValidator<ValidPriceRange, BooksFilterRequestDto> {

    @Override
    public boolean isValid(BooksFilterRequestDto booksFilterRequestDto, ConstraintValidatorContext context) {
        return booksFilterRequestDto.getMinPrice() <= booksFilterRequestDto.getMaxPrice();
    }
}

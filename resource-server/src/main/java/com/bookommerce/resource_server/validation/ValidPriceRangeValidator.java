package com.bookommerce.resource_server.validation;

import com.bookommerce.resource_server.dto.request.BooksFilterRequestDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for the {@link ValidPriceRange} constraint.
 * <p>
 * Checks if the minimum price in {@link BooksFilterRequestDto} is less than or
 * equal to the maximum price.
 * </p>
 */
public class ValidPriceRangeValidator implements ConstraintValidator<ValidPriceRange, BooksFilterRequestDto> {

    /**
     * Validates the price range.
     *
     * @param value   the DTO containing min and max prices.
     * @param context the context in which the constraint is evaluated.
     * @return true if minPrice <= maxPrice, false otherwise.
     */
    @Override
    public boolean isValid(BooksFilterRequestDto value, ConstraintValidatorContext context) {
        return value.getMinPrice() <= value.getMaxPrice();
    }
}

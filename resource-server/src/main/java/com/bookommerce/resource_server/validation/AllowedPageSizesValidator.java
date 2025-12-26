package com.bookommerce.resource_server.validation;

import java.util.stream.IntStream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Validator for the {@link AllowedPageSizes} constraint.
 * <p>
 * Checks if the provided integer value is one of the allowed page sizes (10,
 * 20, 50, 100).
 * </p>
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AllowedPageSizesValidator implements ConstraintValidator<AllowedPageSizes, Integer> {

    /**
     * Array of allowed page sizes.
     */
    int[] allowedPageSizes;

    /**
     * Validates that the page size is allowed.
     *
     * @param value   the value to validate.
     * @param context the context in which the constraint is evaluated.
     * @return true if the value is valid, false otherwise.
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return IntStream.of(allowedPageSizes).anyMatch(i -> i == value);
    }

    /**
     * Initializes the validator with the allowed page sizes.
     *
     * @param constraintAnnotation the constraint annotation.
     */
    @Override
    public void initialize(AllowedPageSizes constraintAnnotation) {
        this.allowedPageSizes = constraintAnnotation.value();
    }
}

package com.bookommerce.resource_server.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Constraint annotation to validate that the minimum price is less than or
 * equal to the maximum price.
 * <p>
 * This annotation is applied at the class level, typically on a filter DTO.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidPriceRangeValidator.class)
public @interface ValidPriceRange {
    /**
     * Error message when validation fails.
     *
     * @return the error message.
     */
    String message() default "Min price must be less than or equal to max price";

    /**
     * Groups the constraint belongs to.
     *
     * @return the groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload associated with the constraint.
     *
     * @return the payload.
     */
    Class<? extends Payload>[] payload() default {};
}

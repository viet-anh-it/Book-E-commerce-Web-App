package com.bookommerce.resource_server.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Constraint annotation to validate that a page size is one of the allowed
 * values.
 * <p>
 * Allowed values are typically 10, 20, 50, and 100.
 * </p>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AllowedPageSizesValidator.class)
public @interface AllowedPageSizes {

    /**
     * Array of allowed page sizes.
     *
     * @return the allowed page sizes.
     */
    int[] value();

    /**
     * Error message when validation fails.
     *
     * @return the error message.
     */
    String message() default "Invalid page size";

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

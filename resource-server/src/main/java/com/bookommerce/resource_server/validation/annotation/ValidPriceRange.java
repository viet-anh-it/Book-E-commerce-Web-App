package com.bookommerce.resource_server.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bookommerce.resource_server.validation.validator.PriceRangeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PriceRangeValidator.class)
public @interface ValidPriceRange {

    String message() default "Min price must be less than or equal to max price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

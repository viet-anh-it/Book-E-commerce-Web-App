package com.bookommerce.resource_server.validation.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AllowedPageSizesValidator.class)
public @interface AllowedPageSizes {

    int[] value();

    String message() default "Invalid page size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

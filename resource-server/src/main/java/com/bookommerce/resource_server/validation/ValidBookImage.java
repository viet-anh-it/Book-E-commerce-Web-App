package com.bookommerce.resource_server.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(value = { ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { ValidBookImageValidator.class })
public @interface ValidBookImage {
    String message() default "Invalid book image";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

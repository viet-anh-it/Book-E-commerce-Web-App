package com.bookommerce.resource_server.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bookommerce.resource_server.validation.validator.UpdateBookImageValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { UpdateBookImageValidator.class })
public @interface ValidUpdateBookImage {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

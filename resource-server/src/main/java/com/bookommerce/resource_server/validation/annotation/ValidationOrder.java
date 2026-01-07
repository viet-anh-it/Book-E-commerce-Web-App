package com.bookommerce.resource_server.validation.annotation;

import jakarta.validation.GroupSequence;

// @formatter:off
@GroupSequence(value = { ValidationOrder._1.class, ValidationOrder._2.class })
public interface ValidationOrder {
    interface _1 {}
    interface _2 {}
}

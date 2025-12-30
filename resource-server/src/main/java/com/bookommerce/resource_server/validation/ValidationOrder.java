package com.bookommerce.resource_server.validation;

import jakarta.validation.GroupSequence;

@GroupSequence(value = { ValidationOrder._1.class, ValidationOrder._2.class })
public interface ValidationOrder {
    //@formatter:off
    interface _1 {}
    interface _2 {}
    //@formatter:on
}

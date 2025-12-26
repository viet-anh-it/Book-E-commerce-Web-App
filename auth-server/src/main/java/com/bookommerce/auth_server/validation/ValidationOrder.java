package com.bookommerce.auth_server.validation;

import com.bookommerce.auth_server.validation.ValidationOrder._1;
import com.bookommerce.auth_server.validation.ValidationOrder._2;

import jakarta.validation.GroupSequence;

@GroupSequence(value = { _1.class, _2.class })
public interface ValidationOrder {
    //@formatter:off
    interface _1 {}
    interface _2 {}
    //@formatter:on
}

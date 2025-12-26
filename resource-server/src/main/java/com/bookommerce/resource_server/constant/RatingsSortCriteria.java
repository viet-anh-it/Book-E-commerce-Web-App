package com.bookommerce.resource_server.constant;

import com.bookommerce.resource_server.entity.Rating_;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RatingsSortCriteria {
    POINT(Rating_.POINT),
    CREATED_AT(Rating_.CREATED_AT);

    String property;
}

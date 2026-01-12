package com.bookommerce.be_for_fe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

//@formatter:off
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiSuccessResponse<T>(
    int status,
    String message,
    T data
) {
    @Builder
    public record  PagingSortingMeta(
        int page,
        int size,
        boolean last,

        @JsonProperty(value = "total")
        long totalElements
    ) {
    }
}

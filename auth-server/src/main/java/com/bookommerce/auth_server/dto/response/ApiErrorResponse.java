package com.bookommerce.auth_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

//@formatter:off
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse<T>(
    int status,
    String message,
    T error
) {}

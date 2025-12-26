package com.bookommerce.resource_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Standard API success response.
 * <p>
 * This class is used to return successful response data to the client.
 * </p>
 *
 * @param <T> the type of the data payload.
 */
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiSuccessResponse<T> {
    /**
     * HTTP status code.
     */
    int status;

    /**
     * Success message.
     */
    String message;

    /**
     * The response data payload.
     */
    T data;

    /**
     * Pagination and sorting metadata (optional).
     */
    PagingAndSortingMetadata meta;
}

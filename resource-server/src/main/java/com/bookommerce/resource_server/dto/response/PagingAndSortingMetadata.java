package com.bookommerce.resource_server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Metadata for pagination and sorting.
 * <p>
 * This class contains information about the current page, page size, and
 * whether it is the last page.
 * </p>
 */
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingAndSortingMetadata {
    /**
     * Current page number (0-based).
     */
    int page;

    /**
     * Number of items per page.
     */
    int size;

    /**
     * Indicates if this is the last page.
     */
    boolean last;

    /**
     * Total number of elements.
     */
    @JsonProperty(value = "total")
    long totalElements;
}

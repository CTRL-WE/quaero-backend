package com.ctrlwe.quaero.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standardised error response returned by the global exception handler.
 *
 * <p>Follows RFC 7807–inspired conventions while remaining lightweight.
 * Every error response includes a timestamp, HTTP status code, short error
 * label, descriptive message, and the request path that triggered the error.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ApiErrorResponse error = ApiErrorResponse.builder()
 *         .status(404)
 *         .error("Not Found")
 *         .message("User with id 42 does not exist")
 *         .path("/api/v1/users/42")
 *         .build();
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    /**
     * Timestamp indicating when the error occurred.
     */
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code (e.g. 400, 404, 500).
     */
    private final int status;

    /**
     * Short error label (e.g. "Bad Request", "Not Found").
     */
    private final String error;

    /**
     * Human-readable description of what went wrong.
     */
    private final String message;

    /**
     * The request URI that caused the error.
     */
    private final String path;
}

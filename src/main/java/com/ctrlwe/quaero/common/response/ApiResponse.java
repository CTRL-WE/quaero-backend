package com.ctrlwe.quaero.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper used across all REST endpoints.
 *
 * <p>Provides a consistent envelope for every successful and failed response,
 * carrying an HTTP status code, a human-readable message, an optional data
 * payload, and metadata such as a timestamp and success flag.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
 *         .success(true)
 *         .status(200)
 *         .message("User retrieved successfully")
 *         .data(userDto)
 *         .build();
 * }</pre>
 *
 * @param <T> the type of the response payload
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Timestamp indicating when the response was generated.
     */
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Whether the operation completed successfully.
     */
    private final boolean success;

    /**
     * HTTP status code associated with the response.
     */
    private final int status;

    /**
     * Human-readable message describing the result.
     */
    private final String message;

    /**
     * Optional payload returned by the operation.
     */
    private final T data;

    /**
     * Creates a successful {@code ApiResponse} with data.
     *
     * @param data    the response payload
     * @param message a human-readable success message
     * @param status  the HTTP status code
     * @param <T>     the type of the payload
     * @return a fully populated success response
     */
    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates a successful {@code ApiResponse} with a 200 status.
     *
     * @param data    the response payload
     * @param message a human-readable success message
     * @param <T>     the type of the payload
     * @return a fully populated success response with HTTP 200
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return success(data, message, 200);
    }

    /**
     * Creates a failure {@code ApiResponse} without data.
     *
     * @param message a human-readable error message
     * @param status  the HTTP status code
     * @param <T>     the type of the payload (will be {@code null})
     * @return a failure response
     */
    public static <T> ApiResponse<T> failure(String message, int status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .build();
    }
}

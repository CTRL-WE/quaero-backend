package com.ctrlwe.quaero.exception;

import lombok.Getter;

/**
 * Thrown when a requested resource cannot be found in the system.
 *
 * <p>This exception is typically raised by service-layer look-ups
 * (e.g. finding a user or case by ID) and is mapped to HTTP 404
 * by the {@link GlobalExceptionHandler}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * throw new ResourceNotFoundException(
 *         "User with id 42 does not exist",
 *         ErrorCode.RESOURCE_NOT_FOUND);
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see ErrorCode#RESOURCE_NOT_FOUND
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    /** The application-level error code associated with this exception. */
    private final ErrorCode errorCode;

    /**
     * Constructs a new {@code ResourceNotFoundException}.
     *
     * @param message   a human-readable description of the missing resource
     * @param errorCode the {@link ErrorCode} categorising this error
     */
    public ResourceNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

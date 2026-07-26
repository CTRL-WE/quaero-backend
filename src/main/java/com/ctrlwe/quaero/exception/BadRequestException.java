package com.ctrlwe.quaero.exception;

import lombok.Getter;

/**
 * Thrown when a client request is malformed or contains invalid data
 * that cannot be processed.
 *
 * <p>This exception is mapped to HTTP 400 by the
 * {@link GlobalExceptionHandler}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * throw new BadRequestException(
 *         "Email address format is invalid",
 *         ErrorCode.BAD_REQUEST);
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see ErrorCode#BAD_REQUEST
 */
@Getter
public class BadRequestException extends RuntimeException {

    /** The application-level error code associated with this exception. */
    private final ErrorCode errorCode;

    /**
     * Constructs a new {@code BadRequestException}.
     *
     * @param message   a human-readable description of the bad request
     * @param errorCode the {@link ErrorCode} categorising this error
     */
    public BadRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

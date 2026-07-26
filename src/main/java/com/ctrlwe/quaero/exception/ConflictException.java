package com.ctrlwe.quaero.exception;

import lombok.Getter;

/**
 * Thrown when a request conflicts with the current state of a resource
 * (e.g. duplicate email during registration, concurrent modification).
 *
 * <p>This exception is mapped to HTTP 409 by the
 * {@link GlobalExceptionHandler}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * throw new ConflictException(
 *         "A user with this email already exists",
 *         ErrorCode.CONFLICT);
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see ErrorCode#CONFLICT
 */
@Getter
public class ConflictException extends RuntimeException {

    /** The application-level error code associated with this exception. */
    private final ErrorCode errorCode;

    /**
     * Constructs a new {@code ConflictException}.
     *
     * @param message   a human-readable description of the conflict
     * @param errorCode the {@link ErrorCode} categorising this error
     */
    public ConflictException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

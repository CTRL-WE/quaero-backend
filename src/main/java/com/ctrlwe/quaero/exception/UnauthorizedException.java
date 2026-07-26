package com.ctrlwe.quaero.exception;

import lombok.Getter;

/**
 * Thrown when a request lacks valid authentication credentials or
 * when the supplied credentials are invalid.
 *
 * <p>This exception is mapped to HTTP 401 by the
 * {@link GlobalExceptionHandler}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * throw new UnauthorizedException(
 *         "Invalid or expired authentication token",
 *         ErrorCode.UNAUTHORIZED);
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see ErrorCode#UNAUTHORIZED
 */
@Getter
public class UnauthorizedException extends RuntimeException {

    /** The application-level error code associated with this exception. */
    private final ErrorCode errorCode;

    /**
     * Constructs a new {@code UnauthorizedException}.
     *
     * @param message   a human-readable description of the authentication failure
     * @param errorCode the {@link ErrorCode} categorising this error
     */
    public UnauthorizedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

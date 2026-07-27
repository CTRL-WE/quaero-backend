package com.ctrlwe.quaero.user;

import com.ctrlwe.quaero.exception.ConflictException;
import com.ctrlwe.quaero.exception.ErrorCode;

/**
 * Thrown when an attempt is made to create a user with an email or
 * username that already exists in the system.
 *
 * <p>This exception extends {@link ConflictException} and is mapped
 * to HTTP 409 by the {@link com.ctrlwe.quaero.exception.GlobalExceptionHandler}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * throw new UserAlreadyExistsException(
 *         "A user with email 'john@example.com' already exists");
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see ConflictException
 */
public class UserAlreadyExistsException extends ConflictException {

    /**
     * Constructs a new {@code UserAlreadyExistsException} with the
     * specified detail message.
     *
     * <p>The error code is always {@link ErrorCode#CONFLICT}.</p>
     *
     * @param message a human-readable description of the conflict
     */
    public UserAlreadyExistsException(String message) {
        super(message, ErrorCode.CONFLICT);
    }
}

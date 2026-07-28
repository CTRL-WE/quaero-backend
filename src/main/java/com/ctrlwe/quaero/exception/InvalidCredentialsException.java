package com.ctrlwe.quaero.exception;

/**
 * Thrown when a login attempt fails because the supplied email or
 * password is incorrect.
 *
 * <p>This exception is distinct from {@link UnauthorizedException}
 * so that the global exception handler can log it with a credential-
 * specific message and, in future, apply rate-limiting or audit
 * logic without affecting other 401 paths.</p>
 *
 * <p>Mapped to HTTP 401 by
 * {@link GlobalExceptionHandler#handleInvalidCredentials}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public class InvalidCredentialsException extends UnauthorizedException {

    /**
     * Constructs a new {@code InvalidCredentialsException} with the
     * given detail message.
     *
     * @param message a human-readable description of the failure
     */
    public InvalidCredentialsException(String message) {
        super(message, ErrorCode.UNAUTHORIZED);
    }
}

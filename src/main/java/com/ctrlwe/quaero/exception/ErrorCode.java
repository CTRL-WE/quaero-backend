package com.ctrlwe.quaero.exception;

/**
 * Enumeration of standardised application error codes used throughout
 * the Quaero platform.
 *
 * <p>Each constant represents a distinct category of error that the
 * global exception handler maps to an appropriate HTTP status code.
 * Using a centralised enum ensures consistent error reporting across
 * all modules and simplifies client-side error handling.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum ErrorCode {

    /** The requested resource could not be found. */
    RESOURCE_NOT_FOUND,

    /** The request was malformed or contained invalid data. */
    BAD_REQUEST,

    /** Authentication is required or the supplied credentials are invalid. */
    UNAUTHORIZED,

    /** The request conflicts with the current state of the resource. */
    CONFLICT,

    /** One or more request fields failed bean-validation constraints. */
    VALIDATION_ERROR,

    /** An unexpected internal error occurred on the server. */
    INTERNAL_SERVER_ERROR
}

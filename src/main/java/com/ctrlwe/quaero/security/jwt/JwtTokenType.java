package com.ctrlwe.quaero.security.jwt;

/**
 * Enumeration of JWT token types used by the Quaero platform.
 *
 * <p>Each type is embedded as a custom claim ({@code "type"}) inside
 * the token payload so that the validator can distinguish between
 * access tokens and refresh tokens and reject tokens used in the
 * wrong context.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum JwtTokenType {

    /** Short-lived token used to authenticate API requests. */
    ACCESS,

    /** Long-lived token used to obtain a new access token. */
    REFRESH
}

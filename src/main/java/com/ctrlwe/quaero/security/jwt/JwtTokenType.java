package com.ctrlwe.quaero.security.jwt;

/**
 * Enumeration of JWT token types used by the Quaero platform.
 *
 * <p>The type is embedded as a custom {@code "type"} claim inside
 * the token payload so that the validator can distinguish access
 * tokens from any other token type and reject misuse.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum JwtTokenType {

    /** Short-lived token used to authenticate API requests. */
    ACCESS
}

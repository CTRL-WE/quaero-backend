package com.ctrlwe.quaero.security;

/**
 * Central repository for security-related constants used throughout
 * the Quaero platform.
 *
 * <p>Externalising these values into a single class avoids magic
 * strings scattered across filters, configurations, and utilities,
 * and makes future changes (e.g. header names, token prefixes)
 * a one-line update.</p>
 *
 * <p>This class is not meant to be instantiated.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Utility class – prevent instantiation
    }

    /**
     * HTTP header used to transmit the authentication token.
     *
     * <p>Value: {@value}</p>
     */
    public static final String JWT_HEADER = "Authorization";

    /**
     * Prefix that precedes the JWT in the {@link #JWT_HEADER} value.
     *
     * <p>Value: {@value}</p>
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * URL patterns that are accessible without authentication.
     *
     * <p>These patterns are used by both the {@link SecurityConfig}
     * and any filter that needs to skip authentication for public
     * resources.</p>
     */
    public static final String[] PUBLIC_ENDPOINTS = {
            "/",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/auth/**"
    };
}

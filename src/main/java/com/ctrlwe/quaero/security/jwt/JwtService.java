package com.ctrlwe.quaero.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * High-level JWT service facade for the Quaero platform.
 *
 * <p>This service is the <strong>only</strong> JWT component that
 * should be injected by controllers and other application-layer
 * classes. It delegates to the lower-level {@link JwtTokenProvider},
 * {@link JwtTokenValidator}, and {@link JwtClaimsExtractor}
 * internally, hiding implementation details and making it easy to
 * swap or extend the token strategy without affecting consumers.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * String accessToken  = jwtService.generateAccessToken("john.doe");
 * String refreshToken = jwtService.generateRefreshToken("john.doe");
 *
 * boolean valid = jwtService.validateAccessToken(accessToken);
 * String  user  = jwtService.extractUsername(accessToken);
 * }</pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenValidator jwtTokenValidator;
    private final JwtClaimsExtractor jwtClaimsExtractor;

    /**
     * Generates a short-lived access token for the given username.
     *
     * @param username the principal identifier
     * @return a compact, signed JWT access token
     */
    public String generateAccessToken(String username) {
        return jwtTokenProvider.generateAccessToken(username);
    }

    /**
     * Generates a long-lived refresh token for the given username.
     *
     * @param username the principal identifier
     * @return a compact, signed JWT refresh token
     */
    public String generateRefreshToken(String username) {
        return jwtTokenProvider.generateRefreshToken(username);
    }

    /**
     * Validates an access token (signature, expiration, issuer,
     * and token type).
     *
     * @param token the compact JWT string
     * @return {@code true} if the token is a valid access token
     */
    public boolean validateAccessToken(String token) {
        return jwtTokenValidator.validateToken(token, JwtTokenType.ACCESS);
    }

    /**
     * Validates a refresh token (signature, expiration, issuer,
     * and token type).
     *
     * @param token the compact JWT string
     * @return {@code true} if the token is a valid refresh token
     */
    public boolean validateRefreshToken(String token) {
        return jwtTokenValidator.validateToken(token, JwtTokenType.REFRESH);
    }

    /**
     * Extracts the username (subject claim) from the token.
     *
     * @param token the compact JWT string
     * @return the username stored in the {@code sub} claim
     */
    public String extractUsername(String token) {
        return jwtClaimsExtractor.extractSubject(token);
    }
}

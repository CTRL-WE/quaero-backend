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
 * internally.</p>
 *
 * <p>Only access tokens are issued; the platform does not use
 * refresh tokens.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * String token = jwtService.generateAccessToken("john.doe");
 * boolean valid = jwtService.validateAccessToken(token);
 * String  user  = jwtService.extractUsername(token);
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
     * @return a compact, signed JWT access token (valid for 24 hours)
     */
    public String generateAccessToken(String username) {
        return jwtTokenProvider.generateAccessToken(username);
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
     * Extracts the username (subject claim) from the token.
     *
     * @param token the compact JWT string
     * @return the username stored in the {@code sub} claim
     */
    public String extractUsername(String token) {
        return jwtClaimsExtractor.extractSubject(token);
    }
}

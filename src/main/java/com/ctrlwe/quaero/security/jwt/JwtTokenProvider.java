package com.ctrlwe.quaero.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Responsible for generating signed JWT access tokens for the Quaero platform.
 *
 * <p>This class handles the low-level token construction using the
 * JJWT library. Only <strong>access</strong> tokens are generated;
 * refresh tokens are not part of the platform's authentication model.</p>
 *
 * <p>The generated token embeds the following claims:</p>
 * <ul>
 *   <li>{@code sub} – the username / principal identifier</li>
 *   <li>{@code iss} – the configured issuer</li>
 *   <li>{@code iat} – issued-at timestamp</li>
 *   <li>{@code exp} – expiration timestamp (24 hours)</li>
 *   <li>{@code type} – custom claim set to {@link JwtTokenType#ACCESS}</li>
 * </ul>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * Generates a short-lived access token for the given username.
     *
     * @param username the subject to embed in the token
     * @return a compact, signed JWT string (valid for 24 hours)
     */
    public String generateAccessToken(String username) {
        return buildToken(username, JwtTokenType.ACCESS,
                jwtProperties.getAccessTokenExpiration());
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Builds a signed JWT with the given parameters.
     *
     * @param username       the subject claim
     * @param tokenType      the custom {@code type} claim
     * @param expirationMs   the token lifetime in milliseconds
     * @return a compact JWT string
     */
    private String buildToken(String username,
                              JwtTokenType tokenType,
                              long expirationMs) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .claims(Map.of("type", tokenType.name()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Derives the HMAC-SHA signing key from the configured
     * Base64-encoded secret.
     *
     * @return the {@link SecretKey} used for token signing
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}

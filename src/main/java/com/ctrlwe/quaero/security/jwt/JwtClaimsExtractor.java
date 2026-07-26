package com.ctrlwe.quaero.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/**
 * Responsible for extracting claims from a signed JWT.
 *
 * <p>This class centralises all claim-extraction logic so that
 * neither the provider nor the validator need to duplicate parsing
 * code. Every public method delegates to
 * {@link #extractAllClaims(String)} which performs signature
 * verification before returning the payload.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtClaimsExtractor {

    private final JwtProperties jwtProperties;

    /**
     * Extracts the subject ({@code sub}) claim from the token.
     *
     * @param token the compact JWT string
     * @return the subject value
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration ({@code exp}) claim from the token.
     *
     * @param token the compact JWT string
     * @return the expiration {@link Date}
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the issuer ({@code iss}) claim from the token.
     *
     * @param token the compact JWT string
     * @return the issuer value
     */
    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    /**
     * Extracts the token type custom claim ({@code "type"}).
     *
     * @param token the compact JWT string
     * @return the {@link JwtTokenType} stored in the token, or {@code null}
     *         if the claim is absent
     */
    public JwtTokenType extractTokenType(String token) {
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return type != null ? JwtTokenType.valueOf(type) : null;
    }

    /**
     * Extracts a single claim using the supplied resolver function.
     *
     * @param token          the compact JWT string
     * @param claimsResolver a function that selects the desired claim
     * @param <T>            the claim value type
     * @return the resolved claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and verifies the token, returning the full set of claims.
     *
     * @param token the compact JWT string
     * @return the {@link Claims} payload
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Derives the HMAC-SHA signing key from the configured secret.
     *
     * @return the {@link SecretKey} used for signature verification
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtProperties.getSecret());
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }
}

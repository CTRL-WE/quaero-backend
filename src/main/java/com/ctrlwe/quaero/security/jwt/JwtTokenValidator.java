package com.ctrlwe.quaero.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Validates JWT tokens for the Quaero platform.
 *
 * <p>Validation checks performed:</p>
 * <ol>
 *   <li><strong>Signature</strong> – verified implicitly by the JJWT
 *       parser when extracting claims.</li>
 *   <li><strong>Expiration</strong> – the {@code exp} claim must be
 *       in the future.</li>
 *   <li><strong>Issuer</strong> – the {@code iss} claim must match
 *       the configured issuer.</li>
 *   <li><strong>Token type</strong> – the custom {@code type} claim
 *       must match the expected {@link JwtTokenType}.</li>
 * </ol>
 *
 * <p>If any check fails the method returns {@code false} and logs a
 * warning. No exceptions are propagated to the caller.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtClaimsExtractor claimsExtractor;
    private final JwtProperties jwtProperties;

    /**
     * Validates the supplied token against the expected token type.
     *
     * @param token             the compact JWT string to validate
     * @param expectedTokenType the {@link JwtTokenType} the token must
     *                          carry in its {@code type} claim
     * @return {@code true} if the token passes all validation checks,
     *         {@code false} otherwise
     */
    public boolean validateToken(String token, JwtTokenType expectedTokenType) {
        try {
            // 1. Signature – verified implicitly during claim extraction.

            // 2. Expiration
            Date expiration = claimsExtractor.extractExpiration(token);
            if (expiration.before(new Date())) {
                log.warn("JWT token is expired");
                return false;
            }

            // 3. Issuer
            String issuer = claimsExtractor.extractIssuer(token);
            if (!jwtProperties.getIssuer().equals(issuer)) {
                log.warn("JWT issuer mismatch: expected={}, actual={}",
                        jwtProperties.getIssuer(), issuer);
                return false;
            }

            // 4. Token type
            JwtTokenType actualType = claimsExtractor.extractTokenType(token);
            if (expectedTokenType != actualType) {
                log.warn("JWT token type mismatch: expected={}, actual={}",
                        expectedTokenType, actualType);
                return false;
            }

            return true;

        } catch (ExpiredJwtException ex) {
            log.warn("JWT token is expired: {}", ex.getMessage());
        } catch (JwtException ex) {
            log.warn("JWT token validation failed: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT token is null or empty: {}", ex.getMessage());
        }

        return false;
    }
}

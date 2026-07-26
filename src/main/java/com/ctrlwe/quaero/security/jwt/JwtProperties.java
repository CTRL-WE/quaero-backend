package com.ctrlwe.quaero.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalised JWT configuration properties for the Quaero platform.
 *
 * <p>Values are bound from the application configuration file under
 * the {@code jwt} prefix:</p>
 * <pre>
 * jwt.secret=your-base64-encoded-secret
 * jwt.issuer=quaero-backend
 * jwt.access-token-expiration=900000
 * jwt.refresh-token-expiration=604800000
 * </pre>
 *
 * <p>Externalising these values allows different environments
 * (development, staging, production) to use distinct signing keys
 * and token lifetimes without code changes.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Base64-encoded secret key used to sign and verify JWTs.
     *
     * <p>The secret must be at least 256 bits (32 bytes) to satisfy
     * the HMAC-SHA256 minimum key length requirement.</p>
     */
    private String secret;

    /**
     * The {@code iss} (issuer) claim embedded in every token.
     */
    private String issuer;

    /**
     * Access-token lifetime in <strong>milliseconds</strong>.
     *
     * <p>Typical default: 900 000 ms (15 minutes).</p>
     */
    private long accessTokenExpiration;

    /**
     * Refresh-token lifetime in <strong>milliseconds</strong>.
     *
     * <p>Typical default: 604 800 000 ms (7 days).</p>
     */
    private long refreshTokenExpiration;
}

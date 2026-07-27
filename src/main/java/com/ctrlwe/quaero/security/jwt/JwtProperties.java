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
 * jwt.secret=${JWT_SECRET:local-dev-secret-please-override}
 * jwt.issuer=quaero-backend
 * jwt.access-token-expiration=86400000
 * </pre>
 *
 * <p>The secret MUST be set via the {@code JWT_SECRET} environment
 * variable in every non-development environment. The default value
 * in {@code application.properties} is intentionally weak and must
 * never be used in staging or production.</p>
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
     * the HMAC-SHA256 minimum key length requirement. Set this via
     * the {@code JWT_SECRET} environment variable — never hardcode it.</p>
     */
    private String secret;

    /**
     * The {@code iss} (issuer) claim embedded in every token.
     */
    private String issuer;

    /**
     * Access-token lifetime in <strong>milliseconds</strong>.
     *
     * <p>Default: 86 400 000 ms (24 hours).</p>
     */
    private long accessTokenExpiration;
}

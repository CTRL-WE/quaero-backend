package com.ctrlwe.quaero.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response payload returned after a successful login or registration.
 *
 * <p>Contains the JWT access token (valid for 24 hours) and the
 * token type so that clients know how to present it in subsequent
 * requests ({@code Authorization: Bearer …}).</p>
 *
 * <p>No refresh token is included — the platform does not use a
 * refresh-token mechanism. Users re-authenticate when the access
 * token expires.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT access token for authenticating API requests (24-hour lifetime).
     */
    private String accessToken;

    /**
     * The token type, always {@code "Bearer"}.
     */
    @Builder.Default
    private String tokenType = "Bearer";
}

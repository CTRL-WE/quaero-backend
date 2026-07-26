package com.ctrlwe.quaero.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response payload returned after a successful login.
 *
 * <p>Contains the JWT access token, refresh token, and the token
 * type identifier so that clients know how to present the token
 * in subsequent requests (e.g. {@code Authorization: Bearer …}).</p>
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
     * Short-lived JWT access token for authenticating API requests.
     */
    private String accessToken;

    /**
     * Long-lived JWT refresh token for obtaining a new access token.
     */
    private String refreshToken;

    /**
     * The token type, typically {@code "Bearer"}.
     */
    @Builder.Default
    private String tokenType = "Bearer";
}

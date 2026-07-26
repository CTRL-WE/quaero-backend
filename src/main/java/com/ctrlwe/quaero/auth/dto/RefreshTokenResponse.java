package com.ctrlwe.quaero.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Response payload returned after a successful token refresh.
 *
 * <p>Contains a newly generated access token and the original (or
 * rotated) refresh token, along with the token type.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {

    /**
     * The newly issued short-lived JWT access token.
     */
    private String accessToken;

    /**
     * The refresh token (same or rotated).
     */
    private String refreshToken;

    /**
     * The token type, typically {@code "Bearer"}.
     */
    @Builder.Default
    private String tokenType = "Bearer";
}

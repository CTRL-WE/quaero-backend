package com.ctrlwe.quaero.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for refreshing an access token.
 *
 * <p>Contains the long-lived refresh token that was issued during
 * login. The server validates the refresh token and, if valid,
 * issues a new access token.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    /**
     * The JWT refresh token to exchange for a new access token.
     */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}

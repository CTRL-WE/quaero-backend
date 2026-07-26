package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RefreshTokenResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper responsible for converting between auth-related DTOs and
 * internal service models.
 *
 * <p>Keeping mapping logic in a dedicated component ensures that
 * controllers and services remain decoupled from each other's
 * representation, and makes it straightforward to adapt when the
 * User entity or additional profile fields are introduced.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class AuthMapper {

    /**
     * Builds a {@link LoginResponse} from the generated tokens.
     *
     * @param accessToken  the JWT access token
     * @param refreshToken the JWT refresh token
     * @return a fully populated {@link LoginResponse}
     */
    public LoginResponse toLoginResponse(String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Builds a {@link RefreshTokenResponse} from the generated tokens.
     *
     * @param accessToken  the newly issued JWT access token
     * @param refreshToken the refresh token (same or rotated)
     * @return a fully populated {@link RefreshTokenResponse}
     */
    public RefreshTokenResponse toRefreshTokenResponse(String accessToken, String refreshToken) {
        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper responsible for converting auth-related service data into
 * response DTOs.
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class AuthMapper {

    /**
     * Builds a {@link LoginResponse} from the generated access token.
     *
     * @param accessToken the JWT access token
     * @return a fully populated {@link LoginResponse}
     */
    public LoginResponse toLoginResponse(String accessToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}

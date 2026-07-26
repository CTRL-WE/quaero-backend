package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.RefreshTokenRequest;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;
import com.ctrlwe.quaero.exception.BadRequestException;
import com.ctrlwe.quaero.exception.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Validates authentication request payloads beyond what Bean
 * Validation annotations cover.
 *
 * <p>This component performs <em>stateless</em> validation only —
 * no database lookups, no external service calls. Examples include
 * cross-field consistency checks and format verifications that
 * cannot be expressed with standard constraint annotations.</p>
 *
 * <p>When the User entity and repository are introduced, duplicate-email
 * and duplicate-username checks should be added to the service layer
 * rather than here.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class AuthValidator {

    /**
     * Validates a {@link LoginRequest}.
     *
     * <p>Currently verifies that the email and password are
     * syntactically acceptable. Bean Validation handles the basic
     * {@code @NotBlank} / {@code @Email} / {@code @Size} constraints;
     * this method is reserved for any additional cross-field rules.</p>
     *
     * @param request the login request to validate
     * @throws BadRequestException if validation fails
     */
    public void validateLoginRequest(LoginRequest request) {
        // Bean Validation handles @NotBlank, @Email, @Size.
        // Additional cross-field checks can be added here.
    }

    /**
     * Validates a {@link RegisterRequest}.
     *
     * <p>Ensures the username does not contain whitespace beyond what
     * the {@code @Size} annotation covers.</p>
     *
     * @param request the registration request to validate
     * @throws BadRequestException if validation fails
     */
    public void validateRegisterRequest(RegisterRequest request) {
        if (request.getUsername() != null && request.getUsername().contains(" ")) {
            throw new BadRequestException(
                    "Username must not contain spaces",
                    ErrorCode.VALIDATION_ERROR);
        }
    }

    /**
     * Validates a {@link RefreshTokenRequest}.
     *
     * <p>Ensures the refresh token string is not blank. Bean Validation
     * handles the {@code @NotBlank} constraint; this method is reserved
     * for additional format checks.</p>
     *
     * @param request the refresh token request to validate
     * @throws BadRequestException if validation fails
     */
    public void validateRefreshTokenRequest(RefreshTokenRequest request) {
        // Bean Validation handles @NotBlank.
        // Additional token-format checks can be added here.
    }
}

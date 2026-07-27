package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;
import com.ctrlwe.quaero.exception.BadRequestException;
import com.ctrlwe.quaero.exception.ErrorCode;
import org.springframework.stereotype.Component;

/**
 * Validates authentication request payloads beyond what Bean
 * Validation annotations cover.
 *
 * <p>This component performs <em>stateless</em> validation only —
 * no database lookups, no external service calls.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class AuthValidator {

    /**
     * Validates a {@link LoginRequest}.
     *
     * <p>Bean Validation handles {@code @NotBlank}, {@code @Email},
     * and {@code @Size} constraints. This method is reserved for
     * any additional cross-field rules.</p>
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
     * <p>Ensures the username does not contain whitespace beyond
     * what the {@code @Size} annotation covers.</p>
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
}

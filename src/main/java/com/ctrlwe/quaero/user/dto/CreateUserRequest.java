package com.ctrlwe.quaero.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating a new user account.
 *
 * <p>Carries the information required to register a new user.
 * All fields are mandatory and are validated automatically by the
 * Bean Validation framework before reaching the service layer.</p>
 *
 * <p>This DTO is used by the service layer for programmatic user
 * creation. It does <em>not</em> perform authentication or JWT
 * generation — those concerns belong to the Auth module.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    /**
     * The user's chosen display name.
     * Must be between 3 and 50 characters with no whitespace.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * The user's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    /**
     * The user's plain-text password (will be encoded before persistence).
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    /**
     * The user's full display name (optional).
     */
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;
}

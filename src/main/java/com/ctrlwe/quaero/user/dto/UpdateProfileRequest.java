package com.ctrlwe.quaero.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating an existing user's profile.
 *
 * <p>All fields are optional — only non-{@code null} fields will be
 * applied to the user entity. This allows partial updates without
 * requiring the client to send the entire profile.</p>
 *
 * <p>Sensitive fields such as {@code email}, {@code username}, and
 * {@code password} are <em>not</em> included in this DTO. Those
 * changes require dedicated workflows with additional validation
 * (e.g. email verification, password confirmation).</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    /**
     * The updated full display name (optional, max 100 characters).
     */
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    /**
     * The updated profile picture URL (optional, max 500 characters).
     */
    @Size(max = 500, message = "Profile picture URL must not exceed 500 characters")
    private String profilePictureUrl;

    /**
     * The updated biographical text (optional, max 1000 characters).
     */
    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
}

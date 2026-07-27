package com.ctrlwe.quaero.user.dto;

import com.ctrlwe.quaero.user.AccountStatus;
import com.ctrlwe.quaero.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response payload representing a user's full public profile.
 *
 * <p>Contains all user-visible profile information. Sensitive fields
 * such as {@code password} are <strong>never</strong> included.
 * This DTO is returned by the {@code GET /api/users/me} endpoint.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /** The user's unique identifier. */
    private Long id;

    /** The user's display name. */
    private String username;

    /** The user's email address. */
    private String email;

    /** The user's full display name. */
    private String fullName;

    /** URL pointing to the user's profile picture. */
    private String profilePictureUrl;

    /** Short biographical text. */
    private String bio;

    /** The user's authorisation role. */
    private Role role;

    /** The account's lifecycle status. */
    private AccountStatus accountStatus;

    /** The user's cumulative reputation score. */
    private int reputationScore;

    /** Timestamp when the account was created. */
    private LocalDateTime createdAt;

    /** Timestamp of the most recent profile update. */
    private LocalDateTime updatedAt;
}

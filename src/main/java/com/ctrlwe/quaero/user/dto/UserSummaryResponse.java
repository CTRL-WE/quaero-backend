package com.ctrlwe.quaero.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Lightweight response payload representing a user summary.
 *
 * <p>Contains only the essential fields needed for display in
 * lists, cards, and cross-module references (e.g. showing the
 * author of a submission). Suitable for embedding in other
 * module responses without exposing the full profile.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    /** The user's unique identifier. */
    private Long id;

    /** The user's display name. */
    private String username;

    /** URL pointing to the user's profile picture. */
    private String profilePictureUrl;

    /** The user's cumulative reputation score. */
    private int reputationScore;
}

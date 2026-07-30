package com.ctrlwe.quaero.user.dto;

import com.ctrlwe.quaero.reputation.RankTier;
import com.ctrlwe.quaero.user.AccountStatus;
import com.ctrlwe.quaero.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
@Builder(toBuilder = true)
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

    // ── Reputation & Progression fields (composed in by UserController) ──────────

    /**
     * Total XP accumulated across all accepted submissions.
     * {@code 0} until the first submission. Managed by ReputationService.
     */
    @Schema(description = "Total XP accumulated by the user.", example = "340")
    private int totalXp;

    /**
     * Running arithmetic mean of all accepted reasoning scores.
     * {@code null} until the user's first accepted submission.
     */
    @Schema(description = "Running credibility score (mean of reasoning scores). " +
            "Null until first submission.", example = "74.5000", nullable = true)
    private BigDecimal credibility;

    /** Number of investigation sessions completed. */
    @Schema(description = "Number of completed investigation sessions.", example = "5")
    private int completedInvestigations;

    /** Number of accepted (successful) submissions. */
    @Schema(description = "Number of successful submissions.", example = "5")
    private int successfulSubmissions;

    /**
     * Cosmetic rank tier derived from {@link #totalXp} at read time.
     * Never stored in the database.
     */
    @Schema(description = "Cosmetic rank tier derived from XP (never persisted).",
            example = "Analyst")
    private RankTier rankTier;

    /**
     * 1-indexed position in the leaderboard (credibility DESC, XP DESC).
     * Deliberately named {@code leaderboardPosition} to avoid confusion with
     * the cosmetic {@link #rankTier}.
     */
    @Schema(description = "1-indexed leaderboard position.", example = "3")
    private int leaderboardPosition;
}

package com.ctrlwe.quaero.reputation.dto;

import com.ctrlwe.quaero.reputation.RankTier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Carries all progression-related statistics for a single user's profile view.
 *
 * <p>This DTO is assembled by {@code ReputationServiceImpl.getProfileStatistics(userId)}
 * and is composed into the existing {@link com.ctrlwe.quaero.user.dto.UserProfileResponse}
 * by {@code UserServiceImpl.getProfile()}. The six fields listed here are added
 * <em>additively</em> to the profile response — no existing field is renamed or removed.</p>
 *
 * <h3>Fields</h3>
 * <ul>
 *   <li>{@link #totalXp} — cumulative XP from the {@code users} table.</li>
 *   <li>{@link #credibility} — running mean of reasoning scores; {@code null} until first
 *       submission.</li>
 *   <li>{@link #completedInvestigations} — count of investigation sessions completed.</li>
 *   <li>{@link #successfulSubmissions} — count of accepted submissions.</li>
 *   <li>{@link #rankTier} — derived cosmetic tier; <strong>never persisted</strong>.</li>
 *   <li>{@link #leaderboardPosition} — 1-indexed rank within the full user list ordered
 *       by credibility DESC, then XP DESC.</li>
 * </ul>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Progression statistics composited into a user's profile response.")
public class ProfileStatistics {

    /** Cumulative XP total earned across all submissions. */
    @Schema(description = "Total XP accumulated by the user.", example = "340")
    private int totalXp;

    /**
     * Running arithmetic mean of all accepted reasoning scores.
     * {@code null} until the user's first submission is recorded.
     */
    @Schema(description = "Running credibility score (arithmetic mean of reasoning scores). " +
            "Null until the first submission.", example = "74.5000")
    private BigDecimal credibility;

    /** Number of investigation sessions completed by this user. */
    @Schema(description = "Number of completed investigation sessions.", example = "5")
    private int completedInvestigations;

    /** Number of accepted (successful) submissions. */
    @Schema(description = "Number of successful submissions.", example = "5")
    private int successfulSubmissions;

    /**
     * Cosmetic rank tier derived from {@link #totalXp} at read time.
     * Never stored in the database.
     */
    @Schema(description = "Cosmetic rank tier derived from total XP. Never persisted.",
            example = "Analyst")
    private RankTier rankTier;

    /**
     * 1-indexed position of this user in the full leaderboard ordered by
     * credibility DESC, then XP DESC.
     *
     * <p>Deliberately named {@code leaderboardPosition} — not {@code rank} — to
     * avoid confusion with the cosmetic {@link #rankTier}.</p>
     */
    @Schema(description = "1-indexed leaderboard position (credibility DESC, XP DESC).",
            example = "3")
    private int leaderboardPosition;
}

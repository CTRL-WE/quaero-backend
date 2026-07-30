package com.ctrlwe.quaero.reputation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * A single entry in the public leaderboard.
 *
 * <p>Returned as an element of the list produced by
 * {@code GET /api/leaderboard}. The full list is ordered by
 * credibility DESC, then XP DESC before mapping — so the
 * {@link #position} field simply mirrors each entry's 1-based index
 * in that pre-sorted list.</p>
 *
 * <h3>Field naming note</h3>
 * <p>The position field is deliberately named {@code position} (not {@code rank}),
 * to avoid semantic collision with the cosmetic rank-tier concept returned by the
 * Profile endpoint.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A single entry in the platform leaderboard, ordered by " +
        "credibility DESC then XP DESC.")
public class LeaderboardEntryResponse {

    /**
     * The user's display name.
     */
    @Schema(description = "The user's display name (username).", example = "alice_investigates")
    private String username;

    /**
     * 1-indexed position in the leaderboard (1 = highest credibility).
     * Not named {@code rank} to avoid confusion with the cosmetic rank tier.
     */
    @Schema(description = "1-indexed position in the leaderboard.", example = "1")
    private int position;

    /**
     * The user's total accumulated XP.
     */
    @Schema(description = "Total XP accumulated by this user.", example = "340")
    private int xp;

    /**
     * The user's running credibility score (arithmetic mean of all reasoning scores).
     * {@code null} if the user has no accepted submissions yet.
     */
    @Schema(description = "Running credibility score. Null if no submissions yet.",
            example = "74.5000")
    private BigDecimal credibility;

    /**
     * Number of investigation sessions completed by this user.
     */
    @Schema(description = "Number of completed investigation sessions.", example = "5")
    private int completedInvestigations;
}

package com.ctrlwe.quaero.reputation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Carries the progression data that {@code ReputationService.recordSubmissionOutcome()}
 * returns to its caller (currently {@code SubmissionServiceImpl.createSubmission()}).
 *
 * <p>At minimum this DTO must carry:</p>
 * <ul>
 *   <li>{@link #xpEarned} — the XP awarded for <em>this specific submission</em>
 *       (not the running total), so the submission response can report "you earned X XP".</li>
 *   <li>{@link #updatedCredibility} — the user's new running credibility after this
 *       submission, so the submission response can report the current standing.</li>
 *   <li>{@link #newTotalXp} — the user's updated cumulative XP total, useful for
 *       display purposes in a richer feedback response.</li>
 * </ul>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result returned after recording a submission outcome, " +
        "carrying the progression change for this specific submission.")
public class ProgressionUpdateResult {

    /**
     * The XP earned specifically for this submission (not the running total).
     * Equals {@code ReputationCalculator.calculateXp(reasoningScore)}.
     */
    @Schema(description = "XP earned for this specific submission.", example = "42")
    private int xpEarned;

    /**
     * The user's updated cumulative XP after this submission has been applied.
     */
    @Schema(description = "User's new cumulative XP total.", example = "142")
    private int newTotalXp;

    /**
     * The user's updated running credibility after this submission has been applied.
     * {@code null} is structurally impossible here — the service always writes a value
     * before returning this DTO — but the type is nullable for forward compatibility.
     */
    @Schema(description = "User's updated credibility score (arithmetic mean of all reasoning scores).",
            example = "76.3333")
    private BigDecimal updatedCredibility;
}

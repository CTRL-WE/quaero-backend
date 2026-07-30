package com.ctrlwe.quaero.reputation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pure, stateless calculation functions for the Reputation &amp; Progression system.
 *
 * <p>This class has <strong>no Spring dependencies</strong> and no side-effects — every
 * method is a deterministic function of its arguments. It must never be annotated with
 * {@code @Component}, {@code @Service}, or any other Spring stereotype, so that it can
 * be unit-tested in total isolation without a Spring context.</p>
 *
 * <h3>Business rules encoded here</h3>
 * <ol>
 *   <li>{@link #calculateXp(int)} — fixed completion bonus + score-derived bonus, capped at 50.</li>
 *   <li>{@link #calculateCredibility(BigDecimal, int, int)} — running arithmetic mean of all
 *       historical reasoning scores, with explicit handling of the first-submission case.</li>
 *   <li>{@link #deriveRankTier(int)} — cosmetic {@link RankTier} label computed from total XP.
 *       Never persisted — always derived at read time.</li>
 * </ol>
 *
 * <h3>Why an enum for rank tier?</h3>
 * <p>A {@link RankTier} enum is used instead of a plain {@code String} so that:
 * (a) the full set of valid tiers is visible at compile time,
 * (b) serialisation into the API response produces a predictable string value,
 * and (c) callers cannot accidentally pass an invalid tier label. The enum's
 * {@code displayName} field holds the human-readable label that the front-end renders.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class ReputationCalculator {

    /** Maximum XP that a single submission can contribute. */
    public static final int MAX_XP_PER_SUBMISSION = 50;

    /** Fixed completion bonus awarded for every accepted submission. */
    private static final int COMPLETION_BONUS = 10;

    /** Scaling factor applied to reasoningScore to derive the variable bonus. */
    private static final double SCORE_MULTIPLIER = 0.4;

    // Private constructor — this is a utility class; instantiation is forbidden.
    private ReputationCalculator() {
        throw new UnsupportedOperationException(
                "ReputationCalculator is a utility class and must not be instantiated.");
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  1. XP calculation
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Calculates the XP earned for a single submission.
     *
     * <p>Formula: {@code 10 + round(reasoningScore × 0.4)}, capped at
     * {@value #MAX_XP_PER_SUBMISSION}.</p>
     *
     * <p>Rounding: {@link Math#round(double)} is used, which applies "half-up"
     * rounding (rounds .5 towards positive infinity). The cap is applied
     * <em>after</em> rounding.</p>
     *
     * @param reasoningScore the AI-assigned reasoning score for this submission
     *                       (expected range 0–100, but not validated here)
     * @return the XP earned, between {@value #COMPLETION_BONUS} and
     *         {@value #MAX_XP_PER_SUBMISSION} inclusive
     */
    public static int calculateXp(int reasoningScore) {
        long rawXp = COMPLETION_BONUS + Math.round(reasoningScore * SCORE_MULTIPLIER);
        return (int) Math.min(rawXp, MAX_XP_PER_SUBMISSION);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  2. Credibility calculation
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Calculates the user's new credibility score after adding one more submission.
     *
     * <p>Formula: {@code (currentCredibility × totalSubmissions + newReasoningScore)
     * / (totalSubmissions + 1)}.</p>
     *
     * <p><strong>First-submission case:</strong> when {@code currentCredibility} is
     * {@code null} <em>or</em> {@code totalSubmissions} is {@code 0}, the result is
     * simply {@code newReasoningScore} cast to {@link BigDecimal}. This avoids both a
     * {@link NullPointerException} and a divide-by-zero path.</p>
     *
     * <p>The result is scaled to 4 decimal places with
     * {@link RoundingMode#HALF_UP} to keep the stored value compact while retaining
     * enough precision for leaderboard ordering.</p>
     *
     * @param currentCredibility the user's current credibility, or {@code null}
     *                           if the user has never submitted before
     * @param totalSubmissions   the number of previously accepted submissions for
     *                           this user (before the current one)
     * @param newReasoningScore  the reasoning score awarded for the current submission
     * @return the new credibility value, always non-null
     */
    public static BigDecimal calculateCredibility(BigDecimal currentCredibility,
                                                  int totalSubmissions,
                                                  int newReasoningScore) {
        if (currentCredibility == null || totalSubmissions == 0) {
            // First-ever submission: credibility = the single reasoning score.
            return BigDecimal.valueOf(newReasoningScore);
        }

        // Running arithmetic mean: (old_mean × old_count + new_value) / new_count
        BigDecimal numerator = currentCredibility
                .multiply(BigDecimal.valueOf(totalSubmissions))
                .add(BigDecimal.valueOf(newReasoningScore));

        BigDecimal newCount = BigDecimal.valueOf((long) totalSubmissions + 1);

        return numerator.divide(newCount, 4, RoundingMode.HALF_UP);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  3. Rank tier derivation
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Derives the user's cosmetic {@link RankTier} from their total accumulated XP.
     *
     * <p>This method is called on <em>every read</em> (Profile and Leaderboard).
     * The result is <strong>never stored</strong> in the database — persisting a
     * derived, cosmetic label would create a risk of the stored value diverging from
     * the authoritative formula.</p>
     *
     * @param totalXp the user's total accumulated XP (non-negative)
     * @return the rank tier corresponding to this XP level, never {@code null}
     */
    public static RankTier deriveRankTier(int totalXp) {
        if (totalXp >= 1500) return RankTier.TRUTH_GUARDIAN;
        if (totalXp >= 700)  return RankTier.DETECTIVE;
        if (totalXp >= 300)  return RankTier.ANALYST;
        if (totalXp >= 100)  return RankTier.INVESTIGATOR;
        return RankTier.EXPLORER;
    }
}

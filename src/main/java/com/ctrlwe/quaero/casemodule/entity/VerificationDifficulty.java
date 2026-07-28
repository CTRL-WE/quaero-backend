package com.ctrlwe.quaero.casemodule.entity;

/**
 * Difficulty level for verifying the claim in an Investigation Challenge.
 *
 * <p>Signals investigation difficulty on the Feed card and serves as a
 * natural future hook for the Reputation module (V1 Section 9, V2
 * Section 7): harder cases that are correctly resolved should yield
 * more reputation than easy ones.</p>
 *
 * <p>Additional future uses documented in V1/V2:</p>
 * <ul>
 *   <li>Difficulty-weighted XP bonus in Reputation scoring</li>
 *   <li>"Trending" or "most-investigated" leaderboard weighting</li>
 *   <li>Adaptive difficulty recommendations</li>
 * </ul>
 *
 * <p><strong>Current status:</strong> field is stored in the database
 * with a default of {@link #MEDIUM}. No scoring or filtering logic is
 * built on it yet — it is displayed on the Feed card only.</p>
 *
 * @author Quaero Engineering
 * @since 2.0
 */
public enum VerificationDifficulty {

    /** Straightforward claims with readily available evidence. */
    EASY,

    /** Claims requiring moderate research and cross-referencing. */
    MEDIUM,

    /** Claims requiring deep investigation and source-chain analysis. */
    HARD
}

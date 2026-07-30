package com.ctrlwe.quaero.reputation;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Cosmetic rank tier derived from a user's total XP.
 *
 * <h3>Design rationale — enum vs. String</h3>
 * <p>An enum is used here instead of a plain {@code String} constant for three
 * reasons:</p>
 * <ol>
 *   <li><strong>Closed set</strong> — the valid tier labels are fixed at compile
 *       time; no caller can accidentally supply an invalid value.</li>
 *   <li><strong>Predictable serialisation</strong> — {@link JsonValue} causes
 *       Jackson to serialise each constant as its {@link #displayName} string, so
 *       the JSON payload seen by the front-end uses the human-readable label
 *       (e.g. {@code "Truth Guardian"}) rather than the Java constant name
 *       (e.g. {@code "TRUTH_GUARDIAN"}).</li>
 *   <li><strong>Safe equality checks</strong> — switch expressions and equality
 *       comparisons are type-safe without magic string literals.</li>
 * </ol>
 *
 * <h3>XP thresholds (as defined by the frozen spec)</h3>
 * <table border="1">
 *   <tr><th>Tier</th><th>Minimum XP</th><th>Maximum XP</th></tr>
 *   <tr><td>Explorer</td><td>0</td><td>99</td></tr>
 *   <tr><td>Investigator</td><td>100</td><td>299</td></tr>
 *   <tr><td>Analyst</td><td>300</td><td>699</td></tr>
 *   <tr><td>Detective</td><td>700</td><td>1499</td></tr>
 *   <tr><td>Truth Guardian</td><td>1500</td><td>∞</td></tr>
 * </table>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum RankTier {

    /** XP 0–99. */
    EXPLORER("Explorer"),

    /** XP 100–299. */
    INVESTIGATOR("Investigator"),

    /** XP 300–699. */
    ANALYST("Analyst"),

    /** XP 700–1499. */
    DETECTIVE("Detective"),

    /** XP 1500+. */
    TRUTH_GUARDIAN("Truth Guardian");

    /** Human-readable label returned in JSON responses. */
    private final String displayName;

    RankTier(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable display name for this tier.
     *
     * <p>{@link JsonValue} causes Jackson to use this value when serialising
     * the enum to JSON, so API consumers see {@code "Truth Guardian"} rather
     * than {@code "TRUTH_GUARDIAN"}.</p>
     *
     * @return the display name
     */
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}

package com.ctrlwe.quaero.ai.dto;

/**
 * Structured result returned by
 * {@link com.ctrlwe.quaero.ai.service.AiClientService#getGradingResult}.
 *
 * <p>Encapsulates a numeric score (0–100), narrative feedback, and a
 * {@code degraded} flag that is {@code true} whenever the AI provider
 * was unavailable or returned a malformed response. When
 * {@code degraded=true} the score defaults to {@code 0} and the
 * feedback contains a safe placeholder — callers must never surface
 * these values to end-users as a real grade.</p>
 *
 * @param score    the AI-generated score in the range 0–100,
 *                 or {@code 0} when degraded
 * @param feedback narrative feedback from the AI, or a safe
 *                 placeholder when degraded
 * @param degraded {@code true} if the AI provider was unavailable
 *                 or returned a malformed/empty response
 * @author Quaero Engineering
 * @since 1.0
 */
public record GradingResult(int score, String feedback, boolean degraded) {

    /**
     * Creates a degraded grading result with a neutral score and
     * safe placeholder feedback.
     *
     * @return a {@code GradingResult} with {@code score=0} and
     *         {@code degraded=true}
     */
    public static GradingResult ofDegraded() {
        return new GradingResult(
                0,
                "Grading is temporarily unavailable. Your submission has been recorded " +
                "and will be re-evaluated. Please check back later.",
                true);
    }
}

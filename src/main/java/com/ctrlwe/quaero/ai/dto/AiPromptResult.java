package com.ctrlwe.quaero.ai.dto;

/**
 * Typed result returned by AI Client service methods.
 *
 * <p>Wraps the raw response text with a {@code degraded} flag so that
 * callers can distinguish a genuine AI response from a graceful
 * fallback caused by a Gemini API failure, timeout, or malformed
 * response. Callers must never propagate an exception when
 * {@code degraded=true} — the fallback text is a safe, user-facing
 * placeholder.</p>
 *
 * <p>Factory methods:</p>
 * <ul>
 *   <li>{@link #ok(String)} — successful response</li>
 *   <li>{@link #degraded(String)} — fallback placeholder</li>
 * </ul>
 *
 * @param responseText the AI-generated text, or a fallback placeholder
 *                     when {@code degraded=true}
 * @param degraded     {@code true} if the AI provider was unavailable
 *                     or returned a malformed/empty response
 * @author Quaero Engineering
 * @since 1.0
 */
public record AiPromptResult(String responseText, boolean degraded) {

    /**
     * Creates a successful (non-degraded) result.
     *
     * @param text the genuine AI-generated response text
     * @return an {@code AiPromptResult} with {@code degraded=false}
     */
    public static AiPromptResult ok(String text) {
        return new AiPromptResult(text, false);
    }

    /**
     * Creates a degraded result wrapping a fallback placeholder.
     *
     * @param fallback a safe, user-facing placeholder string
     * @return an {@code AiPromptResult} with {@code degraded=true}
     */
    public static AiPromptResult degraded(String fallback) {
        return new AiPromptResult(fallback, true);
    }
}

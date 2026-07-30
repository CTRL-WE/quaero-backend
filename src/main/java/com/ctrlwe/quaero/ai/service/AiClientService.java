package com.ctrlwe.quaero.ai.service;

import com.ctrlwe.quaero.ai.dto.AiPromptResult;
import com.ctrlwe.quaero.ai.dto.AiRequest;
import com.ctrlwe.quaero.ai.dto.GradingResult;

import java.util.List;

/**
 * Contract for AI-powered text-generation capabilities within the
 * Quaero platform.
 *
 * <p>Exposes exactly two public methods:</p>
 * <ol>
 *   <li>{@link #getSocraticResponse(AiRequest)} — used by the
 *       Investigation module to power the AI Mentor dialogue.</li>
 *   <li>{@link #getGradingResult(String, List, String)} — used by the
 *       Submission &amp; Evaluation module to score a user's verdict.</li>
 * </ol>
 *
 * <p>Both methods return typed result objects that carry a
 * {@code degraded} flag. Callers must never propagate an exception
 * from this interface — all AI provider failures are absorbed here and
 * returned as degraded results.</p>
 *
 * <p>Prompt engineering is handled by
 * {@link com.ctrlwe.quaero.ai.util.PromptBuilder} before calling
 * these methods. The Socratic method accepts a pre-built
 * {@link AiRequest} (with system instruction and multi-turn contents);
 * the grading method accepts raw strings.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface AiClientService {

    /**
     * Generates a Socratic-style response using the given multi-turn
     * request structure.
     *
     * <p>The request includes Gemini's native {@code system_instruction}
     * and a {@code contents[]} array with alternating user/model roles,
     * enabling the model to maintain conversational context and
     * persistent behavioral rules.</p>
     *
     * <p>Never throws. Returns a degraded {@link AiPromptResult}
     * (with {@code degraded=true} and a safe placeholder text) if
     * the AI provider is unavailable or returns a blank response.</p>
     *
     * @param request the fully-formed {@link AiRequest} built by
     *                {@link com.ctrlwe.quaero.ai.util.PromptBuilder#buildSocraticRequest}
     * @return an {@link AiPromptResult} — never {@code null}
     */
    AiPromptResult getSocraticResponse(AiRequest request);

    /**
     * Grades a user's submission against the case evidence.
     *
     * <p>Never throws. Returns a degraded {@link GradingResult}
     * (with {@code score=0} and {@code degraded=true}) if the AI
     * provider is unavailable or the response cannot be parsed.</p>
     *
     * @param rationale      the user's reasoning text
     * @param evidenceLinks  URLs or references cited by the user
     * @param caseContext    the internal case context (claim + ground truth)
     *                       provided by {@code CaseService.getFullContext()}
     * @return a {@link GradingResult} — never {@code null}
     */
    GradingResult getGradingResult(String rationale,
                                   List<String> evidenceLinks,
                                   String caseContext);
}

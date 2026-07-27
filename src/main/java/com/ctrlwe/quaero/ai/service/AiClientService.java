package com.ctrlwe.quaero.ai.service;

import com.ctrlwe.quaero.ai.dto.AiPromptResult;
import com.ctrlwe.quaero.ai.dto.GradingResult;

import java.util.List;

/**
 * Contract for AI-powered text-generation capabilities within the
 * Quaero platform.
 *
 * <p>Exposes exactly two public methods:</p>
 * <ol>
 *   <li>{@link #getSocraticResponse(String)} — used by the
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
 * <p>Prompt engineering must be done via
 * {@link com.ctrlwe.quaero.ai.util.PromptBuilder} before calling
 * these methods; the interface accepts pre-built prompt strings.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface AiClientService {

    /**
     * Generates a Socratic-style response to the given prompt.
     *
     * <p>Never throws. Returns a degraded {@link AiPromptResult}
     * (with {@code degraded=true} and a safe placeholder text) if
     * the AI provider is unavailable or returns a blank response.</p>
     *
     * @param prompt the fully-formed Socratic prompt
     * @return an {@link AiPromptResult} — never {@code null}
     */
    AiPromptResult getSocraticResponse(String prompt);

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

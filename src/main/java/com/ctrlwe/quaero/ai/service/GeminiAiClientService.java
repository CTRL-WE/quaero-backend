package com.ctrlwe.quaero.ai.service;

import com.ctrlwe.quaero.ai.client.GeminiHttpClient;
import com.ctrlwe.quaero.ai.dto.AiPromptResult;
import com.ctrlwe.quaero.ai.dto.AiRequest;
import com.ctrlwe.quaero.ai.dto.AiResponse;
import com.ctrlwe.quaero.ai.dto.GradingResult;
import com.ctrlwe.quaero.ai.exception.AiServiceException;
import com.ctrlwe.quaero.ai.util.PromptBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Google Gemini implementation of the {@link AiClientService} contract.
 *
 * <p>This service delegates every request to {@link GeminiHttpClient}
 * and wraps results in typed value objects ({@link AiPromptResult},
 * {@link GradingResult}). It deliberately contains
 * <strong>no prompt engineering</strong> and <strong>no
 * investigation logic</strong> — those concerns are handled by
 * {@link PromptBuilder} and upstream modules respectively.</p>
 *
 * <p>Neither public method throws. All {@link AiServiceException}s
 * and blank-response cases are absorbed here and returned as
 * degraded results, ensuring that Gemini unavailability never
 * propagates as an exception to callers.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Service
public class GeminiAiClientService implements AiClientService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiClientService.class);

    /**
     * Fallback text returned when Gemini is unavailable for a Socratic exchange.
     * Must be Socratic in tone and must never state or imply a verdict.
     */
    private static final String SOCRATIC_FALLBACK =
            "That's an interesting line of thinking. I'm not able to engage fully " +
            "right now — could you try rephrasing your question or coming back in a " +
            "moment? In the meantime, consider: what sources would you consult to " +
            "test that idea?";

    private final GeminiHttpClient geminiHttpClient;

    /**
     * Constructs a new {@code GeminiAiClientService}.
     *
     * @param geminiHttpClient the low-level HTTP client for the Gemini API
     */
    public GeminiAiClientService(GeminiHttpClient geminiHttpClient) {
        this.geminiHttpClient = geminiHttpClient;
    }

    // ------------------------------------------------------------------
    // Public interface
    // ------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Sends the prompt to Gemini and wraps the response in an
     * {@link AiPromptResult}. Returns {@code degraded=true} if Gemini
     * is unreachable or returns a blank/malformed response.</p>
     */
    @Override
    public AiPromptResult getSocraticResponse(String prompt) {
        log.debug("Generating Socratic response");
        try {
            String text = executeGeneration(prompt);
            if (text == null || text.isBlank()) {
                log.warn("Gemini returned blank Socratic response; returning degraded result");
                return AiPromptResult.degraded(SOCRATIC_FALLBACK);
            }
            return AiPromptResult.ok(text);
        } catch (AiServiceException ex) {
            log.error("Gemini unavailable for Socratic response: {}", ex.getMessage(), ex);
            return AiPromptResult.degraded(SOCRATIC_FALLBACK);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Builds a grading prompt via {@link PromptBuilder}, sends it to
     * Gemini, parses the score from the response, and returns a
     * {@link GradingResult}. Returns a degraded result on any failure.</p>
     */
    @Override
    public GradingResult getGradingResult(String rationale,
                                          List<String> evidenceLinks,
                                          String caseContext) {
        log.debug("Generating grading result");
        try {
            String prompt = PromptBuilder.buildGradingPrompt(rationale, evidenceLinks, caseContext);
            String text = executeGeneration(prompt);
            if (text == null || text.isBlank()) {
                log.warn("Gemini returned blank grading response; returning degraded result");
                return GradingResult.ofDegraded();
            }
            return parseGradingResponse(text);
        } catch (AiServiceException ex) {
            log.error("Gemini unavailable for grading: {}", ex.getMessage(), ex);
            return GradingResult.ofDegraded();
        }
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /**
     * Executes a single generation call and returns the raw text.
     *
     * @param prompt the fully-formed prompt string
     * @return the first candidate's text, or an empty string if the
     *         response structure is empty
     * @throws AiServiceException if the HTTP call to Gemini fails
     */
    private String executeGeneration(String prompt) {
        AiRequest request = AiRequest.of(prompt);
        AiResponse response = geminiHttpClient.generate(request);
        return response.extractText();
    }

    /**
     * Parses the AI's grading narrative into a {@link GradingResult}.
     *
     * <p>Looks for a line beginning with {@code "SCORE:"} (case-insensitive)
     * and treats the remainder as the numeric score. All other lines are
     * treated as feedback. If no score line is found, defaults to 50 with
     * the full text as feedback (non-degraded, partial parse).</p>
     *
     * @param text the raw grading response from Gemini
     * @return a parsed {@link GradingResult}
     */
    private GradingResult parseGradingResponse(String text) {
        int score = 50; // default if not parseable
        StringBuilder feedback = new StringBuilder();

        for (String line : text.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.toUpperCase().startsWith("SCORE:")) {
                try {
                    String scoreStr = trimmed.substring(6).trim().replaceAll("[^0-9]", "");
                    if (!scoreStr.isEmpty()) {
                        score = Math.min(100, Math.max(0, Integer.parseInt(scoreStr)));
                    }
                } catch (NumberFormatException ignored) {
                    // keep default score
                }
            } else {
                feedback.append(trimmed).append(" ");
            }
        }

        String feedbackText = feedback.toString().trim();
        if (feedbackText.isEmpty()) {
            feedbackText = text.trim();
        }

        return new GradingResult(score, feedbackText, false);
    }
}

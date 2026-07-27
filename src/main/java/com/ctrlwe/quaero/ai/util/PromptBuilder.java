package com.ctrlwe.quaero.ai.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Centralised utility for constructing AI prompt strings by loading
 * templates from classpath resource files.
 *
 * <p>All prompt templates are stored under
 * {@code src/main/resources/prompts/} and loaded once at class
 * initialisation. This separates prompt engineering from Java code
 * and makes iterating on prompts possible without recompilation.</p>
 *
 * <p>Methods in this class are pure functions — they accept raw input
 * and return a fully-formed prompt string. No Spring dependencies or
 * side effects are involved.</p>
 *
 * <p>Templates use {@code %s} placeholders filled via
 * {@link String#formatted(Object...)}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class PromptBuilder {

    // ------------------------------------------------------------------
    // Template loading — done once at class init, fail-fast on error
    // ------------------------------------------------------------------

    private static final String MENTOR_TEMPLATE;
    private static final String EVALUATION_TEMPLATE;

    static {
        MENTOR_TEMPLATE     = loadTemplate("/prompts/mentor-prompt.txt");
        EVALUATION_TEMPLATE = loadTemplate("/prompts/evaluation-prompt.txt");
    }

    private PromptBuilder() {
        // Utility class — do not instantiate.
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Builds the Socratic mentor prompt by embedding the assembled
     * context-and-conversation block into the mentor template.
     *
     * <p>The template instructs the model to guide the user through
     * Socratic questioning and explicitly prohibits stating any verdict,
     * even under adversarial pressure.</p>
     *
     * @param contextEnrichedInput the assembled string containing the
     *        case claim, guidance hints, and conversation history —
     *        produced by {@code InvestigationServiceImpl.buildContextEnrichedInput()}
     * @return the fully-formed Socratic prompt string
     */
    public static String buildSocraticPrompt(String contextEnrichedInput) {
        return MENTOR_TEMPLATE.formatted(contextEnrichedInput);
    }

    /**
     * Builds the grading evaluation prompt by embedding the user's
     * rationale, evidence links, and case context into the evaluation
     * template.
     *
     * <p>The template instructs the model to score the submission on
     * five rubric dimensions (Reasoning Structure 35%, Evidence Usage 30%,
     * Critical Thinking 20%, Objectivity 10%, Verdict Alignment 5%) and
     * return the result in {@code SCORE: [n]\n[feedback]} format.</p>
     *
     * @param rationale     the user's written reasoning
     * @param evidenceLinks list of URLs or source references cited
     * @param caseContext   the internal case context string (claim +
     *                      ground truth) from {@code CaseService.getFullContext()}
     * @return the fully-formed grading prompt string
     */
    public static String buildGradingPrompt(String rationale,
                                            List<String> evidenceLinks,
                                            String caseContext) {
        String evidenceFormatted = evidenceLinks == null || evidenceLinks.isEmpty()
                ? "(none provided)"
                : String.join("\n", evidenceLinks);

        return EVALUATION_TEMPLATE.formatted(caseContext, rationale, evidenceFormatted);
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Loads a classpath resource as a UTF-8 string.
     *
     * @param resourcePath the classpath-relative path (e.g.
     *                     {@code "/prompts/mentor-prompt.txt"})
     * @return the full file content as a string
     * @throws ExceptionInInitializerError if the resource is missing
     *         or cannot be read — this is intentional fail-fast behaviour
     */
    private static String loadTemplate(String resourcePath) {
        try (InputStream is = PromptBuilder.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Prompt template not found on classpath: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(
                    "Failed to load AI prompt template [" + resourcePath + "]: " + ex.getMessage());
        }
    }
}

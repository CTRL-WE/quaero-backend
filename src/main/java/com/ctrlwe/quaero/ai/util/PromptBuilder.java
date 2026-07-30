package com.ctrlwe.quaero.ai.util;

import com.ctrlwe.quaero.ai.dto.AiRequest;
import com.ctrlwe.quaero.ai.dto.ChatMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralised utility for constructing AI prompt structures by loading
 * templates from classpath resource files.
 *
 * <p>All prompt templates are stored under
 * {@code src/main/resources/prompts/} and loaded once at class
 * initialisation. This separates prompt engineering from Java code
 * and makes iterating on prompts possible without recompilation.</p>
 *
 * <h3>Prompt architecture (3 layers)</h3>
 * <ol>
 *   <li><strong>System instruction</strong> — permanent identity and
 *       behavioral rules for the AI Mentor, sent via Gemini's native
 *       {@code system_instruction} field.</li>
 *   <li><strong>Case context</strong> — claim, hints, difficulty, and
 *       turn awareness metadata, injected as the first {@code user}
 *       message in the multi-turn conversation.</li>
 *   <li><strong>Conversational turns</strong> — native multi-turn
 *       {@code contents[]} array with alternating user/model roles.</li>
 * </ol>
 *
 * <p>Methods in this class are pure functions — they accept raw input
 * and return a fully-formed prompt string or {@link AiRequest}. No
 * Spring dependencies or side effects are involved.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class PromptBuilder {

    // ------------------------------------------------------------------
    // Template loading — done once at class init, fail-fast on error
    // ------------------------------------------------------------------

    private static final String SYSTEM_INSTRUCTION;
    private static final String CASE_CONTEXT_TEMPLATE;
    private static final String EVALUATION_TEMPLATE;

    static {
        SYSTEM_INSTRUCTION     = loadTemplate("/prompts/system-instruction.txt");
        CASE_CONTEXT_TEMPLATE  = loadTemplate("/prompts/case-context-template.txt");
        EVALUATION_TEMPLATE    = loadTemplate("/prompts/evaluation-prompt.txt");
    }

    private PromptBuilder() {
        // Utility class — do not instantiate.
    }

    // ------------------------------------------------------------------
    // Public API — Socratic mentor
    // ------------------------------------------------------------------

    /**
     * Builds a fully-formed multi-turn {@link AiRequest} for the
     * Socratic mentor exchange.
     *
     * <p>The returned request uses Gemini's 3-layer structure:</p>
     * <ol>
     *   <li>{@code system_instruction} — the permanent mentor identity
     *       loaded from {@code system-instruction.txt}.</li>
     *   <li>First {@code user} message — the case context block built
     *       from the claim, hints, difficulty, category, and turn
     *       metadata. This is NOT a real user message; it is injected
     *       context for the model.</li>
     *   <li>Subsequent alternating {@code user}/{@code model} messages
     *       representing the actual conversation history.</li>
     * </ol>
     *
     * <p><strong>Ground truth isolation:</strong> This method accepts
     * only {@code claim} and {@code investigationHints} from the case
     * context. It must NEVER accept or include {@code groundTruth},
     * {@code groundTruthExplanation}, {@code trustedReferences}, or
     * {@code learningSummary}. If you are modifying this method and
     * are tempted to add internal fields, stop and consult the
     * architecture handbook first.</p>
     *
     * @param claim               the investigable claim text
     * @param investigationHints  guidance notes for the mentor
     * @param category            the claim's topical category (may be null)
     * @param difficulty          the verification difficulty level (may be null)
     * @param conversationHistory ordered list of prior conversation turns,
     *                            each as a {@link TurnEntry} (sender + text).
     *                            Empty list for the first turn.
     * @param currentTurn         the current turn number (1-indexed)
     * @param maxTurns            the hard turn ceiling
     * @return a fully-formed {@link AiRequest} ready to send to Gemini
     */
    public static AiRequest buildSocraticRequest(String claim,
                                                  String investigationHints,
                                                  String category,
                                                  String difficulty,
                                                  List<TurnEntry> conversationHistory,
                                                  int currentTurn,
                                                  int maxTurns) {
        // Build the case context message (first "user" message).
        String caseContext = CASE_CONTEXT_TEMPLATE.formatted(
                claim,
                category != null ? category : "General",
                difficulty != null ? difficulty : "MEDIUM",
                String.valueOf(currentTurn),
                String.valueOf(maxTurns),
                investigationHints
        );

        // Build the multi-turn contents array.
        List<ChatMessage> contents = new ArrayList<>();

        // First message: case context (as "user" role — it's system-injected context).
        contents.add(ChatMessage.userMessage(caseContext));

        // Append the conversation history as alternating user/model turns.
        for (TurnEntry turn : conversationHistory) {
            if ("USER".equals(turn.sender())) {
                contents.add(ChatMessage.userMessage(turn.text()));
            } else {
                contents.add(ChatMessage.modelMessage(turn.text()));
            }
        }

        return AiRequest.ofMultiTurn(SYSTEM_INSTRUCTION, contents);
    }

    /**
     * A simple value carrier for a conversation turn entry.
     *
     * <p>This record decouples PromptBuilder from the Investigation
     * module's entity classes. The caller (InvestigationServiceImpl)
     * maps its {@code ConversationTurn} entities to this record
     * before calling {@link #buildSocraticRequest}.</p>
     *
     * @param sender the sender identifier — either {@code "USER"} or {@code "AI"}
     * @param text   the message text
     */
    public record TurnEntry(String sender, String text) {
    }

    // ------------------------------------------------------------------
    // Public API — Grading / evaluation
    // ------------------------------------------------------------------

    /**
     * Builds the grading evaluation prompt by embedding the user's
     * rationale, evidence links, case context, and investigation
     * metadata into the evaluation template.
     *
     * <p>The template instructs the model to score the submission on
     * five rubric dimensions (Reasoning Structure 35%, Evidence Usage 30%,
     * Critical Thinking 20%, Objectivity 10%, Verdict Alignment 5%) and
     * return the result in {@code SCORE: [n]\n[feedback]} format.</p>
     *
     * @param rationale      the user's written reasoning
     * @param evidenceLinks  list of URLs or source references cited
     * @param caseContext    the internal case context string (claim +
     *                       ground truth) from {@code CaseService.getFullContext()}
     * @param turnCount      number of investigation turns completed
     * @param durationMinutes approximate investigation duration in minutes
     * @return the fully-formed grading prompt string
     */
    public static String buildGradingPrompt(String rationale,
                                            List<String> evidenceLinks,
                                            String caseContext,
                                            int turnCount,
                                            long durationMinutes) {
        String evidenceFormatted = evidenceLinks == null || evidenceLinks.isEmpty()
                ? "(none provided)"
                : String.join("\n", evidenceLinks);

        return EVALUATION_TEMPLATE.formatted(
                String.valueOf(turnCount),
                String.valueOf(durationMinutes),
                caseContext,
                rationale,
                evidenceFormatted
        );
    }

    /**
     * Backward-compatible overload that defaults turn metadata to
     * unknown values. Used when investigation metadata is not available.
     *
     * @param rationale     the user's written reasoning
     * @param evidenceLinks list of URLs or source references cited
     * @param caseContext   the internal case context string
     * @return the fully-formed grading prompt string
     */
    public static String buildGradingPrompt(String rationale,
                                            List<String> evidenceLinks,
                                            String caseContext) {
        return buildGradingPrompt(rationale, evidenceLinks, caseContext, 0, 0);
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Loads a classpath resource as a UTF-8 string.
     *
     * @param resourcePath the classpath-relative path (e.g.
     *                     {@code "/prompts/system-instruction.txt"})
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

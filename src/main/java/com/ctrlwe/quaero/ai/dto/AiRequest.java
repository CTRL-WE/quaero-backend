package com.ctrlwe.quaero.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request payload sent to the Gemini {@code generateContent} endpoint.
 *
 * <p>Mirrors the Gemini REST API schema. The top-level object contains:</p>
 * <ul>
 *   <li>{@code system_instruction} — an optional persistent instruction
 *       that defines the model's identity and behavioral rules. When
 *       present, the model treats this as a system-level directive
 *       separate from user content.</li>
 *   <li>{@code contents} — an ordered list of {@link ChatMessage} entries
 *       representing the conversation turns.</li>
 * </ul>
 *
 * <p>Example serialised form (multi-turn with system instruction):</p>
 * <pre>{@code
 * {
 *   "system_instruction": {
 *     "parts": [{ "text": "You are an AI Investigation Mentor..." }]
 *   },
 *   "contents": [
 *     { "role": "user",  "parts": [{ "text": "case context..." }] },
 *     { "role": "model", "parts": [{ "text": "Welcome!..." }] },
 *     { "role": "user",  "parts": [{ "text": "I think this is fake" }] }
 *   ]
 * }
 * }</pre>
 *
 * @param systemInstruction optional system instruction for the model's
 *                          persistent behavioral rules (serialised as
 *                          {@code system_instruction} in JSON)
 * @param contents          the ordered conversation messages to send
 * @author Quaero Engineering
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AiRequest(
        @JsonProperty("system_instruction") SystemInstruction systemInstruction,
        List<ChatMessage> contents
) {

    /**
     * The system instruction wrapper matching Gemini's API schema.
     * Contains a list of {@link ChatMessage.Part} entries with the
     * instruction text.
     *
     * @param parts the text parts composing the system instruction
     */
    public record SystemInstruction(List<ChatMessage.Part> parts) {

        /**
         * Creates a system instruction from a single text block.
         *
         * @param text the system instruction text
         * @return a new {@code SystemInstruction}
         */
        public static SystemInstruction of(String text) {
            return new SystemInstruction(List.of(new ChatMessage.Part(text)));
        }
    }

    /**
     * Convenience factory that builds an {@code AiRequest} with a single
     * user message and no system instruction.
     *
     * <p>Used by the grading flow where no multi-turn conversation or
     * persistent system identity is needed.</p>
     *
     * @param prompt the user prompt text
     * @return a ready-to-send {@code AiRequest}
     */
    public static AiRequest of(String prompt) {
        return new AiRequest(null, List.of(ChatMessage.userMessage(prompt)));
    }

    /**
     * Factory that builds a multi-turn {@code AiRequest} with a system
     * instruction and an ordered conversation history.
     *
     * <p>Used by the Socratic mentor flow where the model needs a
     * persistent identity (system instruction) and native multi-turn
     * conversation context.</p>
     *
     * @param systemInstructionText the full system instruction text
     * @param conversationContents  the ordered conversation messages
     *                              (case context + alternating user/model turns)
     * @return a ready-to-send {@code AiRequest}
     */
    public static AiRequest ofMultiTurn(String systemInstructionText,
                                         List<ChatMessage> conversationContents) {
        return new AiRequest(
                SystemInstruction.of(systemInstructionText),
                conversationContents
        );
    }
}

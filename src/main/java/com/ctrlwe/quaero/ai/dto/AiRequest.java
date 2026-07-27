package com.ctrlwe.quaero.ai.dto;

import java.util.List;

/**
 * Request payload sent to the Gemini {@code generateContent} endpoint.
 *
 * <p>Mirrors the Gemini REST API schema where the top-level object contains
 * an ordered list of {@link ChatMessage} entries under the {@code contents}
 * key.</p>
 *
 * <p>Example serialised form:</p>
 * <pre>{@code
 * {
 *   "contents": [
 *     {
 *       "role": "user",
 *       "parts": [{ "text": "Explain quantum computing" }]
 *     }
 *   ]
 * }
 * }</pre>
 *
 * @param contents the ordered conversation messages to send to the model
 * @author Quaero Engineering
 * @since 1.0
 */
public record AiRequest(List<ChatMessage> contents) {

    /**
     * Convenience factory that builds an {@code AiRequest} with a single
     * user message.
     *
     * @param prompt the user prompt text
     * @return a ready-to-send {@code AiRequest}
     */
    public static AiRequest of(String prompt) {
        return new AiRequest(List.of(ChatMessage.userMessage(prompt)));
    }
}

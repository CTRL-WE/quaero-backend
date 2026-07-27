package com.ctrlwe.quaero.ai.dto;

import java.util.List;

/**
 * Represents a single message in a Gemini API conversation.
 *
 * <p>Each {@code ChatMessage} corresponds to one {@code content} entry in the
 * Gemini REST API request/response payload. The {@code role} indicates the
 * originator of the message (e.g. {@code "user"} or {@code "model"}), while
 * {@code parts} holds the actual text segments.</p>
 *
 * <p>This record is intentionally kept immutable and free of framework
 * annotations so it can be serialised by any JSON library on the classpath.</p>
 *
 * @param role  the message originator – typically {@code "user"} or {@code "model"}
 * @param parts the ordered list of content parts for this message
 * @author Quaero Engineering
 * @since 1.0
 */
public record ChatMessage(String role, List<Part> parts) {

    /**
     * A single content part within a {@link ChatMessage}.
     *
     * <p>Currently only plain-text parts are supported; future versions
     * may extend this to include inline data (images, audio, etc.).</p>
     *
     * @param text the textual content of this part
     */
    public record Part(String text) {
    }

    /**
     * Factory method that creates a user-role message containing a single
     * text part.
     *
     * @param text the text content for the user message
     * @return a new {@code ChatMessage} with role {@code "user"}
     */
    public static ChatMessage userMessage(String text) {
        return new ChatMessage("user", List.of(new Part(text)));
    }

    /**
     * Factory method that creates a model-role message containing a single
     * text part.
     *
     * @param text the text content for the model message
     * @return a new {@code ChatMessage} with role {@code "model"}
     */
    public static ChatMessage modelMessage(String text) {
        return new ChatMessage("model", List.of(new Part(text)));
    }
}

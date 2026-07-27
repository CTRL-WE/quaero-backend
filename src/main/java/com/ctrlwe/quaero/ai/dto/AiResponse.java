package com.ctrlwe.quaero.ai.dto;

import java.util.List;

/**
 * Response payload returned by the Gemini {@code generateContent} endpoint.
 *
 * <p>The Gemini API returns one or more {@link Candidate} objects, each
 * containing a {@link Content} with one or more {@link Part} entries.
 * This record models that hierarchy exactly so Jackson can deserialise
 * the response without custom logic.</p>
 *
 * <p>Use {@link #extractText()} for the common case where only the first
 * candidate's first text part is needed.</p>
 *
 * @param candidates the list of generated response candidates
 * @author Quaero Engineering
 * @since 1.0
 */
public record AiResponse(List<Candidate> candidates) {

    /**
     * A single generation candidate returned by the Gemini API.
     *
     * @param content the content payload of this candidate
     */
    public record Candidate(Content content) {
    }

    /**
     * The content wrapper inside a {@link Candidate}.
     *
     * @param parts the ordered text parts making up this content
     * @param role  the originator role (typically {@code "model"})
     */
    public record Content(List<Part> parts, String role) {
    }

    /**
     * A single text part within a {@link Content}.
     *
     * @param text the generated text
     */
    public record Part(String text) {
    }

    /**
     * Extracts the text from the first part of the first candidate.
     *
     * <p>This is the most common access pattern when only a single
     * text response is expected.</p>
     *
     * @return the generated text, or an empty string if the response
     *         structure is empty or {@code null}
     */
    public String extractText() {
        if (candidates == null || candidates.isEmpty()) {
            return "";
        }
        Candidate candidate = candidates.get(0);
        if (candidate.content() == null
                || candidate.content().parts() == null
                || candidate.content().parts().isEmpty()) {
            return "";
        }
        return candidate.content().parts().get(0).text();
    }
}

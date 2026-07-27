package com.ctrlwe.quaero.ai.util;

/**
 * Centralised utility for constructing AI prompt strings.
 *
 * <p>All prompt templates used by the Quaero AI subsystem are defined
 * here, providing a single location for prompt management, review, and
 * iteration. This keeps business-level prompt engineering out of the
 * service layer.</p>
 *
 * <p>Methods in this class are pure functions – they accept raw input
 * and return a fully-formed prompt string. No Spring dependencies or
 * side effects are involved.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class PromptBuilder {

    /** Private constructor to prevent instantiation. */
    private PromptBuilder() {
        // Utility class — do not instantiate.
    }

    /**
     * Builds a Socratic-method prompt that instructs the AI to guide the
     * user toward an answer through targeted questions rather than giving
     * a direct answer.
     *
     * @param userPrompt the original user question or statement
     * @return the fully-formed Socratic prompt string
     */
    public static String buildSocraticPrompt(String userPrompt) {
        return """
                You are a Socratic tutor. Instead of giving a direct answer, \
                guide the user toward understanding by asking thoughtful, \
                probing questions. Keep your questions concise and focused.

                User's input:
                %s"""
                .formatted(userPrompt);
    }

    /**
     * Builds a prompt that instructs the AI to produce a concise,
     * structured summary of the provided evidence text.
     *
     * @param evidenceText the raw evidence content to summarise
     * @return the fully-formed evidence-summarisation prompt string
     */
    public static String buildEvidenceSummaryPrompt(String evidenceText) {
        return """
                Summarise the following evidence concisely. Highlight the key \
                facts, identify any inconsistencies, and note the overall \
                reliability of the information.

                Evidence:
                %s"""
                .formatted(evidenceText);
    }

    /**
     * Builds a prompt that instructs the AI to classify a claim into one
     * of several predefined categories.
     *
     * @param claimText the claim to classify
     * @return the fully-formed claim-classification prompt string
     */
    public static String buildClaimClassificationPrompt(String claimText) {
        return """
                Classify the following claim into exactly one of these \
                categories: VERIFIED, UNVERIFIED, MISLEADING, FALSE, \
                PARTIALLY_TRUE, or INSUFFICIENT_EVIDENCE.

                Respond with the category followed by a brief justification \
                (one to two sentences).

                Claim:
                %s"""
                .formatted(claimText);
    }
}

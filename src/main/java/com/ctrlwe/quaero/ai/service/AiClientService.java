package com.ctrlwe.quaero.ai.service;

/**
 * Contract for AI-powered text-generation capabilities within the
 * Quaero platform.
 *
 * <p>Each method corresponds to a distinct use-case (Socratic
 * dialogue, evidence summarisation, claim classification) and
 * accepts a raw prompt string. Implementations are responsible for
 * delegating to the appropriate AI provider.</p>
 *
 * <p>This interface is intentionally free of any prompt engineering;
 * callers should pre-process their prompts via
 * {@link com.ctrlwe.quaero.ai.util.PromptBuilder} before invoking
 * these methods.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface AiClientService {

    /**
     * Generates a Socratic-style response to the given prompt.
     *
     * @param prompt the fully-formed Socratic prompt
     * @return the AI-generated Socratic response text
     * @throws com.ctrlwe.quaero.ai.exception.AiServiceException
     *         if the AI provider call fails
     */
    String getSocraticResponse(String prompt);

    /**
     * Summarises the evidence described in the given prompt.
     *
     * @param prompt the fully-formed evidence-summarisation prompt
     * @return the AI-generated summary text
     * @throws com.ctrlwe.quaero.ai.exception.AiServiceException
     *         if the AI provider call fails
     */
    String summarizeEvidence(String prompt);

    /**
     * Classifies the claim described in the given prompt.
     *
     * @param prompt the fully-formed claim-classification prompt
     * @return the AI-generated classification result text
     * @throws com.ctrlwe.quaero.ai.exception.AiServiceException
     *         if the AI provider call fails
     */
    String classifyClaim(String prompt);
}

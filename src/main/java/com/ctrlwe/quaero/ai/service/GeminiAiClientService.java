package com.ctrlwe.quaero.ai.service;

import com.ctrlwe.quaero.ai.client.GeminiHttpClient;
import com.ctrlwe.quaero.ai.dto.AiRequest;
import com.ctrlwe.quaero.ai.dto.AiResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Google Gemini implementation of the {@link AiClientService} contract.
 *
 * <p>This service delegates every request to {@link GeminiHttpClient}
 * and extracts the generated text from the response. It deliberately
 * contains <strong>no prompt engineering</strong> and <strong>no
 * investigation logic</strong> — those concerns are handled by
 * {@link com.ctrlwe.quaero.ai.util.PromptBuilder} and upstream
 * modules, respectively.</p>
 *
 * <p>Constructor injection is used for all dependencies, making the
 * class straightforward to unit-test with mocks.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see GeminiHttpClient
 * @see AiClientService
 */
@Service
public class GeminiAiClientService implements AiClientService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiClientService.class);

    private final GeminiHttpClient geminiHttpClient;

    /**
     * Constructs a new {@code GeminiAiClientService}.
     *
     * @param geminiHttpClient the low-level HTTP client for the Gemini API
     */
    public GeminiAiClientService(GeminiHttpClient geminiHttpClient) {
        this.geminiHttpClient = geminiHttpClient;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Sends the prompt to the Gemini API and returns the raw
     * generated text.</p>
     */
    @Override
    public String getSocraticResponse(String prompt) {
        log.debug("Generating Socratic response");
        return executeGeneration(prompt);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Sends the prompt to the Gemini API and returns the raw
     * generated summary text.</p>
     */
    @Override
    public String summarizeEvidence(String prompt) {
        log.debug("Summarising evidence");
        return executeGeneration(prompt);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Sends the prompt to the Gemini API and returns the raw
     * classification text.</p>
     */
    @Override
    public String classifyClaim(String prompt) {
        log.debug("Classifying claim");
        return executeGeneration(prompt);
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Executes a single generation call against the Gemini API and
     * extracts the response text.
     *
     * @param prompt the fully-formed prompt to send
     * @return the extracted text from the first candidate
     */
    private String executeGeneration(String prompt) {
        AiRequest request = AiRequest.of(prompt);
        AiResponse response = geminiHttpClient.generate(request);
        return response.extractText();
    }
}

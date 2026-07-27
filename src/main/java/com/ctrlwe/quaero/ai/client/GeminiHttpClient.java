package com.ctrlwe.quaero.ai.client;

import com.ctrlwe.quaero.ai.config.GeminiProperties;
import com.ctrlwe.quaero.ai.dto.AiRequest;
import com.ctrlwe.quaero.ai.dto.AiResponse;
import com.ctrlwe.quaero.ai.exception.AiServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Low-level HTTP client responsible <em>exclusively</em> for communication
 * with the Google Gemini REST API.
 *
 * <p>This component owns the outbound HTTP call and response deserialisation.
 * It does <strong>not</strong> contain business logic, prompt engineering,
 * or any domain concepts — those belong in higher-level service classes.</p>
 *
 * <p>All configuration (base URL, timeout, API key, model) is resolved
 * from {@link GeminiProperties} via constructor injection.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see GeminiProperties
 */
@Component
public class GeminiHttpClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiHttpClient.class);

    private final RestClient restClient;
    private final GeminiProperties properties;

    /**
     * Constructs a new {@code GeminiHttpClient}.
     *
     * @param restClient the pre-configured {@link RestClient} targeting
     *                   the Gemini API (provided by
     *                   {@link com.ctrlwe.quaero.ai.config.AiConfig})
     * @param properties the bound Gemini configuration properties
     */
    public GeminiHttpClient(RestClient restClient, GeminiProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    /**
     * Sends a content-generation request to the Gemini API and returns
     * the deserialised response.
     *
     * <p>The endpoint path is built from the configured model name and
     * API key:
     * {@code /models/{model}:generateContent?key={apiKey}}</p>
     *
     * @param request the {@link AiRequest} payload to send
     * @return the deserialised {@link AiResponse} from Gemini
     * @throws AiServiceException if the HTTP call fails or the response
     *                            cannot be deserialised
     */
    public AiResponse generate(AiRequest request) {
        String path = String.format(
                "/models/%s:generateContent?key=%s",
                properties.getModel(),
                properties.getApiKey());

        log.debug("Sending generateContent request to model [{}]", properties.getModel());

        try {
            AiResponse response = restClient.post()
                    .uri(path)
                    .body(request)
                    .retrieve()
                    .body(AiResponse.class);

            if (response == null) {
                throw new AiServiceException(
                        "Gemini API returned a null response body");
            }

            log.debug("Received response with {} candidate(s)",
                    response.candidates() != null ? response.candidates().size() : 0);

            return response;
        } catch (RestClientException ex) {
            log.error("Gemini API call failed: {}", ex.getMessage(), ex);
            throw new AiServiceException(
                    "Failed to communicate with Gemini API: " + ex.getMessage(), ex);
        }
    }
}

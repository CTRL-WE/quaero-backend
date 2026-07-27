package com.ctrlwe.quaero.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalised configuration properties for the Google Gemini AI client.
 *
 * <p>All values are bound from the {@code gemini.*} namespace in
 * {@code application.properties} (or any other Spring property source).
 * No API key or sensitive data is hard-coded; everything is read from
 * the environment.</p>
 *
 * <h3>Required properties</h3>
 * <pre>
 * gemini.api-key    = YOUR_API_KEY
 * gemini.model      = gemini-2.0-flash
 * gemini.base-url   = https://generativelanguage.googleapis.com/v1beta
 * gemini.timeout    = 30
 * </pre>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    /** The Google AI Studio API key used to authenticate requests. */
    private String apiKey;

    /** The Gemini model identifier (e.g. {@code gemini-2.0-flash}). */
    private String model;

    /** The base URL of the Gemini REST API. */
    private String baseUrl;

    /** The HTTP request timeout in seconds. */
    private int timeout;

    /**
     * Returns the API key.
     *
     * @return the Gemini API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the API key.
     *
     * @param apiKey the Gemini API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Returns the model identifier.
     *
     * @return the Gemini model name
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model identifier.
     *
     * @param model the Gemini model name
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the base URL of the Gemini API.
     *
     * @return the base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Sets the base URL of the Gemini API.
     *
     * @param baseUrl the base URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns the HTTP request timeout in seconds.
     *
     * @return the timeout value
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the HTTP request timeout in seconds.
     *
     * @param timeout the timeout value
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

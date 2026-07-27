package com.ctrlwe.quaero.ai.config;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Spring configuration for the AI subsystem.
 *
 * <p>Registers a pre-configured {@link RestClient} bean scoped to the
 * Gemini API. The client is built with the base URL and timeout values
 * read from {@link GeminiProperties}, keeping the HTTP layer fully
 * externalised and testable.</p>
 *
 * <p>All AI-related beans should be declared in this class (or composed
 * from it) to maintain a single, discoverable configuration entry
 * point.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(GeminiProperties.class)
public class AiConfig {

    /**
     * Creates a {@link RestClient} pre-configured for the Gemini API.
     *
     * <p>The client is initialised with:</p>
     * <ul>
     *   <li>The base URL from {@link GeminiProperties#getBaseUrl()}</li>
     *   <li>Connection and read timeouts from
     *       {@link GeminiProperties#getTimeout()}</li>
     *   <li>A JSON {@code Content-Type} default header</li>
     * </ul>
     *
     * @param properties the bound Gemini configuration properties
     * @return a ready-to-use {@link RestClient} targeting the Gemini API
     */
    @Bean
    public RestClient geminiRestClient(GeminiProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(properties.getTimeout()));
        factory.setReadTimeout(Duration.ofSeconds(properties.getTimeout()));

        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(factory)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

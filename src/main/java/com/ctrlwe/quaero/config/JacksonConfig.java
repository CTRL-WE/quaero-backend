package com.ctrlwe.quaero.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson {@link ObjectMapper} configuration for the Quaero platform.
 *
 * <p>Ensures that Java 8+ date/time types ({@code LocalDate},
 * {@code LocalDateTime}, {@code Instant}, etc.) are serialised as
 * ISO-8601 strings instead of numeric arrays, which is the default
 * Jackson behaviour.</p>
 *
 * <p>The configured {@link ObjectMapper} is registered as a Spring
 * bean so that it is used consistently across REST controllers,
 * message converters, and any component that injects it.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Configuration
public class JacksonConfig {

    /**
     * Creates and configures the application-wide {@link ObjectMapper}.
     *
     * <ul>
     *   <li>Registers the {@link JavaTimeModule} for Java 8+ date/time
     *       type support.</li>
     *   <li>Disables {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS}
     *       so that dates are written as human-readable ISO-8601 strings
     *       (e.g. {@code "2026-07-26T18:30:00"}).</li>
     * </ul>
     *
     * @return a fully configured {@link ObjectMapper} instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}

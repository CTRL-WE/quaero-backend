package com.ctrlwe.quaero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Central application configuration for the Quaero platform.
 *
 * <p>Provides shared, cross-cutting beans that are not specific to any
 * single module. As the platform grows, additional infrastructure beans
 * (e.g. task executors, cache managers, event publishers) can be
 * registered here.</p>
 *
 * <p>{@link EnableJpaAuditing} activates the automatic population of
 * {@code @CreatedDate} and {@code @LastModifiedDate} fields on JPA
 * entities that use {@code @EntityListeners(AuditingEntityListener.class)}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Configuration
@EnableJpaAuditing
public class AppConfig {

    /**
     * Provides a {@link PasswordEncoder} backed by the BCrypt hashing
     * algorithm.
     *
     * <p>BCrypt automatically handles salt generation and incorporates
     * a configurable work factor, making it well-suited for secure
     * password storage.</p>
     *
     * @return a {@link BCryptPasswordEncoder} instance with the default
     *         strength (log-rounds = 10)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---------------------------------------------------------------
    // Future shared beans (e.g. TaskExecutor, CacheManager) go here.
    // ---------------------------------------------------------------
}

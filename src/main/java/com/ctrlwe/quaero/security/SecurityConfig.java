package com.ctrlwe.quaero.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Core Spring Security configuration for the Quaero platform.
 *
 * <p>Establishes a <strong>stateless</strong>, token-based security
 * posture suitable for a REST API:</p>
 * <ul>
 *   <li>CSRF protection is disabled (stateless / no browser sessions).</li>
 *   <li>CORS is enabled and delegates to the application-level
 *       {@code CorsConfig} {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer}.</li>
 *   <li>Session management is set to {@code STATELESS}.</li>
 *   <li>The {@link JwtAuthenticationFilter} is inserted before
 *       {@link UsernamePasswordAuthenticationFilter}.</li>
 *   <li>Unauthenticated access is handled by
 *       {@link JwtAuthenticationEntryPoint} (HTTP 401).</li>
 *   <li>Insufficient-authority access is handled by
 *       {@link CustomAccessDeniedHandler} (HTTP 403).</li>
 *   <li>Public endpoints defined in {@link SecurityConstants#PUBLIC_ENDPOINTS}
 *       are permitted without authentication; all others require it.</li>
 * </ul>
 *
 * @author Quaero Engineering
 * @since 1.0
 * @see SecurityConstants
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Builds the {@link SecurityFilterChain} bean that governs HTTP
     * security for the entire application.
     *
     * @param http the {@link HttpSecurity} builder provided by Spring
     * @return the fully configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

package com.ctrlwe.quaero.security;

import com.ctrlwe.quaero.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 *   <li>A {@link DaoAuthenticationProvider} is configured with
 *       {@link CustomUserDetailsService} and the platform-wide
 *       {@link PasswordEncoder} for credential verification.</li>
 *   <li>The {@link AuthenticationManager} is exposed as a bean for
 *       any component that needs programmatic authentication.</li>
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
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Configures a {@link DaoAuthenticationProvider} that delegates
     * user lookup to {@link CustomUserDetailsService} and password
     * verification to the platform-wide {@link PasswordEncoder}.
     *
     * <p>This provider is automatically picked up by the
     * {@link AuthenticationManager} and used by any authentication
     * flow that requires credential verification against the
     * database (e.g. form login, programmatic authentication).</p>
     *
     * @return a fully configured {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} as a Spring bean.
     *
     * <p>This allows other components (e.g. authentication services)
     * to inject the manager for programmatic authentication without
     * needing to build their own.</p>
     *
     * @param authenticationConfiguration Spring's auto-configured
     *        authentication configuration
     * @return the platform {@link AuthenticationManager}
     * @throws Exception if the manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

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
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}


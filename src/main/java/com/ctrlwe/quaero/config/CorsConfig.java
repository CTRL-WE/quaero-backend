package com.ctrlwe.quaero.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for the Quaero platform.
 *
 * <p>Registers cross-origin resource sharing rules that apply to every
 * endpoint exposed by the application. During development the allowed
 * origins cover the most common local front-end dev-server ports
 * (React/CRA on {@code 3000} and Vite on {@code 5173}).</p>
 *
 * <p>In production these origins should be replaced with the actual
 * front-end domain(s) via externalised configuration.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Applies CORS mappings to all API paths.
     *
     * <ul>
     *   <li><b>Origins:</b> {@code http://localhost:3000},
     *       {@code http://localhost:5173}</li>
     *   <li><b>Methods:</b> GET, POST, PUT, DELETE, PATCH, OPTIONS</li>
     *   <li><b>Headers:</b> Authorization, Content-Type</li>
     *   <li><b>Credentials:</b> allowed</li>
     * </ul>
     *
     * @param registry the {@link CorsRegistry} provided by Spring MVC
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:5173"
                )
                .allowedMethods(
                        "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
                )
                .allowedHeaders(
                        "Authorization",
                        "Content-Type"
                )
                .allowCredentials(true);
    }
}

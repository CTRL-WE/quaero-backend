package com.ctrlwe.quaero.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger configuration for the Quaero platform.
 *
 * <p>Defines the global API metadata that is rendered by Swagger UI
 * (available at {@code /swagger-ui/index.html}) and exposes a
 * reusable JWT Bearer authentication scheme so that secured endpoints
 * can be tested directly from the documentation UI.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    /** Security scheme identifier referenced by {@code @SecurityRequirement}. */
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Builds the top-level {@link OpenAPI} descriptor.
     *
     * <p>The descriptor includes:</p>
     * <ul>
     *   <li>API title, version, and description</li>
     *   <li>A JWT Bearer security scheme placeholder that can be
     *       referenced by individual endpoint annotations once
     *       authentication is implemented</li>
     * </ul>
     *
     * @return the configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI quaeroOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("QUAERO Backend API")
                        .version("v1.0")
                        .description("Community Driven Media & Information Literacy Platform"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Provide a valid JWT token to access secured endpoints.")));
    }
}

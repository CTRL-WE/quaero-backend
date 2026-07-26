package com.ctrlwe.quaero.security;

import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom {@link AccessDeniedHandler} for the Quaero platform.
 *
 * <p>Invoked when an authenticated user attempts to access a resource
 * for which they lack sufficient authority. Returns a structured
 * {@link ApiErrorResponse} as JSON with HTTP status
 * {@code 403 Forbidden}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Responds with a {@code 403} JSON error when the authenticated user
     * does not have the required permissions.
     *
     * @param request               the request that resulted in the exception
     * @param response              the response to write the error body to
     * @param accessDeniedException the exception that triggered this handler
     * @throws IOException if writing the response body fails
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("Access denied")
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        MAPPER.writeValue(response.getOutputStream(), body);
    }
}

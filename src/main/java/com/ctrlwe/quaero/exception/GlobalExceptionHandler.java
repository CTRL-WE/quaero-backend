package com.ctrlwe.quaero.exception;

import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.investigation.exception.SessionAlreadySubmittedException;
import com.ctrlwe.quaero.investigation.exception.SessionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for the Quaero platform.
 *
 * <p>Centralises exception handling across all {@code @RestController}
 * endpoints. Every handled exception is translated into a consistent
 * {@link ApiErrorResponse} payload so that API consumers receive
 * predictable, machine-readable error structures.</p>
 *
 * <p>The handler covers the following scenarios:</p>
 * <ul>
 *   <li>{@link ResourceNotFoundException} &rarr; 404 Not Found</li>
 *   <li>{@link BadRequestException} &rarr; 400 Bad Request</li>
 *   <li>{@link InvalidCredentialsException} &rarr; 401 Unauthorized (login failure)</li>
 *   <li>{@link UnauthorizedException} &rarr; 401 Unauthorized</li>
 *   <li>{@link ConflictException} &rarr; 409 Conflict</li>
 *   <li>{@link MethodArgumentNotValidException} &rarr; 400 Bad Request (validation)</li>
 *   <li>{@link Exception} &rarr; 500 Internal Server Error (catch-all)</li>
 * </ul>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ResourceNotFoundException} and returns HTTP 404.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles {@link BadRequestException} and returns HTTP 400.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        log.warn("Bad request: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles {@link InvalidCredentialsException} and returns HTTP 401.
     *
     * <p>This handler is registered before the generic
     * {@link UnauthorizedException} handler to ensure that bad-credential
     * login failures are logged with a distinct message for security
     * audit purposes.</p>
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        log.warn("Login failed — invalid credentials for path [{}]", request.getRequestURI());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Handles {@link UnauthorizedException} and returns HTTP 401.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {

        log.warn("Unauthorized access: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Handles {@link ConflictException} and returns HTTP 409.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {

        log.warn("Conflict: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles bean-validation failures raised by {@code @Valid} and returns
     * HTTP 400 with all field-level error messages aggregated into a single,
     * human-readable message.
     *
     * @param ex      the validation exception containing field errors
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String validationMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", validationMessage);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(validationMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles {@link SessionNotFoundException} and returns HTTP 404.
     *
     * <p>Although this exception extends {@link ResourceNotFoundException}
     * (which is already handled above via polymorphism), this explicit handler
     * provides a more specific log message for on-call visibility.</p>
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionNotFound(
            SessionNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Investigation session not found: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles {@link SessionAlreadySubmittedException} and returns HTTP 409.
     *
     * <p>Although this exception extends {@link ConflictException}
     * (which is already handled above via polymorphism), this explicit handler
     * provides a more specific log message for on-call visibility.</p>
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(SessionAlreadySubmittedException.class)
    public ResponseEntity<ApiErrorResponse> handleSessionAlreadySubmitted(
            SessionAlreadySubmittedException ex,
            HttpServletRequest request) {

        log.warn("Attempt to post into a submitted session: {}", ex.getMessage());

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Catch-all handler for any unhandled exception. Returns HTTP 500
     * with a generic error message to avoid leaking internal details.
     *
     * @param ex      the thrown exception
     * @param request the current HTTP request
     * @return a {@link ResponseEntity} containing the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception on [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

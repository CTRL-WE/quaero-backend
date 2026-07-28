package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;
import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing authentication endpoints.
 *
 * <p>Exposes exactly two public endpoints (matched by
 * {@code /api/auth/**} in the security configuration):</p>
 * <ul>
 *   <li>{@code POST /api/auth/login} — authenticate with email + password</li>
 *   <li>{@code POST /api/auth/signup} — register a new account</li>
 * </ul>
 *
 * <p>No refresh-token endpoint exists — the platform issues a single
 * 24-hour access token per login/signup.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints — login and account registration.")
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns a 24-hour JWT access token.
     *
     * @param request the login credentials (email + password)
     * @return HTTP 200 with the access token on success
     */
    @PostMapping("/login")
    @Operation(
        summary = "Login",
        description = "Authenticates the user with email and password. " +
                      "Returns a 24-hour JWT access token on success. " +
                      "Returns 401 if the email is not found or the password does not match."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Login successful — access token returned.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Request body failed validation (e.g. blank email or password).",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Invalid email or password.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Login successful"));
    }

    /**
     * Registers a new user account and returns a 24-hour JWT access token.
     *
     * @param request the registration payload (username, email, password)
     * @return HTTP 201 with the access token on success
     */
    @PostMapping("/signup")
    @Operation(
        summary = "Sign up",
        description = "Registers a new user account with the provided username, email, " +
                      "and password. Returns a 24-hour JWT access token on success. " +
                      "Returns 409 if the email or username is already in use."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Account created successfully — access token returned.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Request body failed validation.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "A user with the supplied email or username already exists.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> signup(
            @Valid @RequestBody RegisterRequest request) {

        LoginResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Registration successful", HttpStatus.CREATED.value()));
    }
}

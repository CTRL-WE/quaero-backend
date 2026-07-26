package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RefreshTokenRequest;
import com.ctrlwe.quaero.auth.dto.RefreshTokenResponse;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;
import com.ctrlwe.quaero.common.response.ApiResponse;
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
 * <p>All endpoints are publicly accessible (matched by
 * {@code /api/auth/**} in the security configuration) and return
 * responses wrapped in {@link ApiResponse}.</p>
 *
 * <table>
 *   <caption>Endpoint summary</caption>
 *   <tr><th>Method</th><th>Path</th><th>Description</th></tr>
 *   <tr><td>POST</td><td>/api/auth/login</td><td>Authenticate a user</td></tr>
 *   <tr><td>POST</td><td>/api/auth/register</td><td>Register a new account</td></tr>
 *   <tr><td>POST</td><td>/api/auth/refresh</td><td>Refresh an access token</td></tr>
 * </table>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param request the login credentials
     * @return a {@link ResponseEntity} containing the tokens
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Login successful"));
    }

    /**
     * Registers a new user account and returns JWT tokens.
     *
     * @param request the registration payload
     * @return a {@link ResponseEntity} containing the tokens
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        LoginResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response, "Registration successful", HttpStatus.CREATED.value()));
    }

    /**
     * Exchanges a valid refresh token for a new access token.
     *
     * @param request the refresh-token payload
     * @return a {@link ResponseEntity} containing the new tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        RefreshTokenResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Token refreshed successfully"));
    }
}

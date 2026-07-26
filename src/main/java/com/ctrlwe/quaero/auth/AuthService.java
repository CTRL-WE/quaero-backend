package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RefreshTokenRequest;
import com.ctrlwe.quaero.auth.dto.RefreshTokenResponse;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;

/**
 * Service interface for authentication operations.
 *
 * <p>Defines the contract for login, registration, and token-refresh
 * workflows. Implementations must handle credential verification,
 * JWT generation, and any required validation.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface AuthService {

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param request the login payload containing email and password
     * @return a {@link LoginResponse} with access and refresh tokens
     */
    LoginResponse login(LoginRequest request);

    /**
     * Registers a new user account.
     *
     * @param request the registration payload containing username,
     *                email, and password
     * @return a {@link LoginResponse} with access and refresh tokens
     *         for the newly created account
     */
    LoginResponse register(RegisterRequest request);

    /**
     * Exchanges a valid refresh token for a new access token.
     *
     * @param request the refresh-token payload
     * @return a {@link RefreshTokenResponse} with a new access token
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}

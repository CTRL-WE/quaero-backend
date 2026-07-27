package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;

/**
 * Service interface for authentication operations.
 *
 * <p>Defines the contract for login and registration workflows.
 * Implementations must handle credential verification, JWT generation,
 * and any required validation.</p>
 *
 * <p>No refresh-token operation is defined — the platform issues a
 * single 24-hour access token and does not rotate tokens.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface AuthService {

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param request the login payload containing email and password
     * @return a {@link LoginResponse} containing the access token
     */
    LoginResponse login(LoginRequest request);

    /**
     * Registers a new user account.
     *
     * @param request the registration payload containing username,
     *                email, and password
     * @return a {@link LoginResponse} containing the access token
     *         for the newly created account
     */
    LoginResponse register(RegisterRequest request);
}

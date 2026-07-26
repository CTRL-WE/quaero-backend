package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RefreshTokenRequest;
import com.ctrlwe.quaero.auth.dto.RefreshTokenResponse;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;
import com.ctrlwe.quaero.exception.BadRequestException;
import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.UnauthorizedException;
import com.ctrlwe.quaero.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link AuthService}.
 *
 * <p>Uses the {@link JwtService} facade for all token operations.
 * In its current <strong>foundation</strong> state, user look-up and
 * credential verification are <em>placeholder</em> implementations;
 * these will be completed once the User entity and repository layers
 * are available.</p>
 *
 * <p>No database access is performed by this class.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final AuthValidator authValidator;

    /**
     * {@inheritDoc}
     *
     * <p><strong>Placeholder:</strong> currently generates tokens for
     * the supplied email without verifying credentials against a
     * persistent store. Replace the TODO section with real
     * authentication once the User entity is available.</p>
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        authValidator.validateLoginRequest(request);

        // TODO: Look up the user by email from the UserRepository.
        // TODO: Verify the password using PasswordEncoder.matches().
        // TODO: Throw UnauthorizedException if credentials are invalid.

        String username = request.getEmail();

        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        log.info("Login successful for user: {}", username);

        return authMapper.toLoginResponse(accessToken, refreshToken);
    }

    /**
     * {@inheritDoc}
     *
     * <p><strong>Placeholder:</strong> currently generates tokens for
     * the supplied email without persisting a new user. Replace the
     * TODO section with real registration logic once the User entity
     * is available.</p>
     */
    @Override
    public LoginResponse register(RegisterRequest request) {
        authValidator.validateRegisterRequest(request);

        // TODO: Check for duplicate email/username via UserRepository.
        // TODO: Encode the password using PasswordEncoder.encode().
        // TODO: Persist the new User entity.

        String username = request.getEmail();

        String accessToken = jwtService.generateAccessToken(username);
        String refreshToken = jwtService.generateRefreshToken(username);

        log.info("Registration successful for user: {}", username);

        return authMapper.toLoginResponse(accessToken, refreshToken);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates the supplied refresh token and, if valid, issues
     * a new access token for the same subject.</p>
     */
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        authValidator.validateRefreshTokenRequest(request);

        String token = request.getRefreshToken();

        if (!jwtService.validateRefreshToken(token)) {
            throw new UnauthorizedException(
                    "Invalid or expired refresh token",
                    ErrorCode.UNAUTHORIZED);
        }

        String username = jwtService.extractUsername(token);
        String newAccessToken = jwtService.generateAccessToken(username);

        log.info("Token refreshed for user: {}", username);

        return authMapper.toRefreshTokenResponse(newAccessToken, token);
    }
}

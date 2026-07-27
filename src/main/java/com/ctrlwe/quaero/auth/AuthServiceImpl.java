package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RefreshTokenRequest;
import com.ctrlwe.quaero.auth.dto.RefreshTokenResponse;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;
import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.UnauthorizedException;
import com.ctrlwe.quaero.security.jwt.JwtService;
import com.ctrlwe.quaero.user.AccountStatus;
import com.ctrlwe.quaero.user.Role;
import com.ctrlwe.quaero.user.User;
import com.ctrlwe.quaero.user.UserAlreadyExistsException;
import com.ctrlwe.quaero.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link AuthService}.
 *
 * <p>Uses the {@link UserRepository} for credential verification and
 * user persistence, {@link PasswordEncoder} for secure password
 * hashing and comparison, and {@link JwtService} for all token
 * operations.</p>
 *
 * <p>Login authenticates an existing user by email and password.
 * Registration creates a new user after validating uniqueness of
 * both email and username. Both operations return a pair of JWT
 * tokens (access + refresh) upon success.</p>
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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     *
     * <p>Looks up the user by email from the {@link UserRepository}.
     * If no user is found, or if the supplied password does not match
     * the stored hash, an {@link UnauthorizedException} is thrown.
     * On success, generates access and refresh tokens keyed to the
     * user's username.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authValidator.validateLoginRequest(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(
                        "Invalid email or password",
                        ErrorCode.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(
                    "Invalid email or password",
                    ErrorCode.UNAUTHORIZED);
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        log.info("Login successful for user: {}", user.getUsername());

        return authMapper.toLoginResponse(accessToken, refreshToken);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates that neither the email nor the username is already
     * taken. Encodes the password with {@link PasswordEncoder}, builds
     * a new {@link User} entity with {@link Role#USER},
     * {@link AccountStatus#ACTIVE}, and a reputation score of zero,
     * then persists it via {@link UserRepository}. On success,
     * generates access and refresh tokens keyed to the new user's
     * username.</p>
     */
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        authValidator.validateRegisterRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "A user with email '" + request.getEmail() + "' already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "A user with username '" + request.getUsername() + "' already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .reputationScore(0)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        log.info("Registration successful for user: {}", user.getUsername());

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


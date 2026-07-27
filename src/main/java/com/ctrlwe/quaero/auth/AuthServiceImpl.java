package com.ctrlwe.quaero.auth;

import com.ctrlwe.quaero.auth.dto.LoginRequest;
import com.ctrlwe.quaero.auth.dto.LoginResponse;
import com.ctrlwe.quaero.auth.dto.RegisterRequest;
import com.ctrlwe.quaero.exception.InvalidCredentialsException;
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
 * both email and username. Both operations return a single 24-hour
 * access token — the platform does not use refresh tokens.</p>
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
     * <p>Looks up the user by email. If no user is found, or if the
     * supplied password does not match the stored hash, an
     * {@link InvalidCredentialsException} is thrown (HTTP 401).
     * On success, generates and returns a 24-hour access token.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authValidator.validateLoginRequest(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername());

        log.info("Login successful for user: {}", user.getUsername());

        return authMapper.toLoginResponse(accessToken);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates that neither the email nor the username is already
     * taken. Encodes the password with {@link PasswordEncoder}, builds
     * a new {@link User} entity with {@link Role#USER},
     * {@link AccountStatus#ACTIVE}, and a reputation score of zero,
     * then persists it via {@link UserRepository}. On success,
     * generates and returns a 24-hour access token.</p>
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
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .accountStatus(AccountStatus.ACTIVE)
                .reputationScore(0)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUsername());

        log.info("Registration successful for user: {}", user.getUsername());

        return authMapper.toLoginResponse(accessToken);
    }
}

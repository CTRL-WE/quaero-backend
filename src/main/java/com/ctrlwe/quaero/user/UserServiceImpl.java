package com.ctrlwe.quaero.user;

import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.ResourceNotFoundException;
import com.ctrlwe.quaero.user.dto.CreateUserRequest;
import com.ctrlwe.quaero.user.dto.UpdateProfileRequest;
import com.ctrlwe.quaero.user.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of {@link UserService}.
 *
 * <p>All DTO construction is delegated to {@link UserMapper} which
 * performs explicit, field-by-field mapping. No generic object mapper
 * is used — this ensures that sensitive fields such as
 * {@code password} are never accidentally leaked.</p>
 *
 * <p>Uniqueness validation is delegated to {@link UserValidator}
 * which checks against the {@link UserRepository} before any
 * persistence operation.</p>
 *
 * <p>Password encoding is performed using the platform-wide
 * {@link PasswordEncoder} bean configured in
 * {@link com.ctrlwe.quaero.config.AppConfig}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     *
     * <p>Validates uniqueness of email and username, encodes the
     * password, persists the new user entity, and returns the
     * created user's profile.</p>
     */
    @Override
    @Transactional
    public UserProfileResponse createUser(CreateUserRequest request) {
        log.debug("createUser called for email={}", request.getEmail());

        userValidator.validateUniqueEmail(request.getEmail());
        userValidator.validateUniqueUsername(request.getUsername());

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User created successfully: id={}, username={}",
                savedUser.getId(), savedUser.getUsername());

        return userMapper.toProfileResponse(savedUser);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Applies only non-{@code null} fields from the update request.
     * The entity is saved and the updated profile is returned.</p>
     */
    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.debug("updateProfile called for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + userId + " does not exist",
                        ErrorCode.RESOURCE_NOT_FOUND));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated for userId={}", userId);

        return userMapper.toProfileResponse(updatedUser);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Looks up the user by ID and throws
     * {@link ResourceNotFoundException} if not found.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        log.debug("getProfile called for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + userId + " does not exist",
                        ErrorCode.RESOURCE_NOT_FOUND));

        return userMapper.toProfileResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileResponse> findByEmail(String email) {
        log.debug("findByEmail called for email={}", email);

        return userRepository.findByEmail(email)
                .map(userMapper::toProfileResponse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileResponse> findByUsername(String username) {
        log.debug("findByUsername called for username={}", username);

        return userRepository.findByUsername(username)
                .map(userMapper::toProfileResponse);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates directly to the repository's JPQL ordering query.
     * No calculation is performed here.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersOrderedByProgression() {
        log.debug("getUsersOrderedByProgression called");
        return userRepository.findAllOrderByCredibilityDescTotalXpDesc();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Loads the entity, overwrites only the four progression fields,
     * and persists. The existing {@code @LastModifiedDate updatedAt}
     * column is refreshed automatically by JPA auditing on save.
     * No XP/credibility arithmetic is performed here.</p>
     */
    @Override
    @Transactional
    public void updateProgressionFields(Long userId,
                                        int newTotalXp,
                                        BigDecimal newCredibility,
                                        int newCompletedInvestigations,
                                        int newSuccessfulSubmissions) {
        log.debug("updateProgressionFields called for userId={}, newTotalXp={}, " +
                "newCredibility={}, completedInvestigations={}, successfulSubmissions={}",
                userId, newTotalXp, newCredibility,
                newCompletedInvestigations, newSuccessfulSubmissions);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + userId + " does not exist",
                        ErrorCode.RESOURCE_NOT_FOUND));

        user.setTotalXp(newTotalXp);
        user.setCredibility(newCredibility);
        user.setCompletedInvestigations(newCompletedInvestigations);
        user.setSuccessfulSubmissions(newSuccessfulSubmissions);

        userRepository.save(user);
        log.info("Progression updated for userId={}: totalXp={}, credibility={}",
                userId, newTotalXp, newCredibility);
    }
}

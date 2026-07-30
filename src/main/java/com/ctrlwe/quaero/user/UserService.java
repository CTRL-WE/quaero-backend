package com.ctrlwe.quaero.user;

import com.ctrlwe.quaero.user.dto.CreateUserRequest;
import com.ctrlwe.quaero.user.dto.UpdateProfileRequest;
import com.ctrlwe.quaero.user.dto.UserProfileResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for the User module.
 *
 * <p>This interface is the <strong>only</strong> permitted entry point
 * into User module logic from outside the module. The cross-module
 * access rule for this codebase states that no module may reach into
 * another module's repository or entity directly — all inter-module
 * calls must go through the owning module's service interface.</p>
 *
 * <p>The methods on this interface cover:</p>
 * <ol>
 *   <li>{@link #createUser(CreateUserRequest)} — programmatic user creation.</li>
 *   <li>{@link #updateProfile(Long, UpdateProfileRequest)} — profile updates.</li>
 *   <li>{@link #getProfile(Long)} — full profile retrieval.</li>
 *   <li>{@link #findByEmail(String)} — lookup by email.</li>
 *   <li>{@link #findByUsername(String)} — lookup by username.</li>
 * </ol>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface UserService {

    /**
     * Creates a new user with the provided details.
     *
     * <p>The password in the request will be encoded before
     * persistence. Uniqueness of email and username is validated
     * before saving.</p>
     *
     * @param request the creation request containing username, email,
     *                password, and optional full name
     * @return the created user's full profile response
     * @throws UserAlreadyExistsException if the email or username
     *         is already taken
     */
    UserProfileResponse createUser(CreateUserRequest request);

    /**
     * Updates the profile of an existing user.
     *
     * <p>Only non-{@code null} fields in the request are applied,
     * allowing partial updates.</p>
     *
     * @param userId  the ID of the user to update
     * @param request the update request containing the fields to modify
     * @return the updated user's full profile response
     * @throws com.ctrlwe.quaero.exception.ResourceNotFoundException
     *         if no user with the given ID exists
     */
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    /**
     * Retrieves the full profile for a user by their ID.
     *
     * @param userId the user's unique identifier
     * @return the user's full profile response
     * @throws com.ctrlwe.quaero.exception.ResourceNotFoundException
     *         if no user with the given ID exists
     */
    UserProfileResponse getProfile(Long userId);

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an {@link Optional} containing the profile response
     *         if found, or empty if no user has the given email
     */
    Optional<UserProfileResponse> findByEmail(String email);

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an {@link Optional} containing the profile response
     *         if found, or empty if no user has the given username
     */
    Optional<UserProfileResponse> findByUsername(String username);

    /**
     * Returns all users ordered by credibility DESC, then total XP DESC.
     *
     * <p>This is the canonical ordering method for both the Leaderboard
     * endpoint and the leaderboard-position lookup inside Profile.
     * No XP or credibility calculation is performed here — the fields
     * are simply read from the database as-is.</p>
     *
     * <p>Users whose {@code credibility} is {@code null} (no submissions yet)
     * are sorted below all users who have a credibility value, consistent
     * with SQL {@code ORDER BY credibility DESC NULLS LAST, total_xp DESC}.</p>
     *
     * @return the full ordered list of users; never {@code null}, may be empty
     */
    List<User> getUsersOrderedByProgression();

    /**
     * Persists updated progression fields for a user.
     *
     * <p>This is the <strong>only</strong> write path for XP and credibility.
     * All calculation is performed externally by
     * {@link com.ctrlwe.quaero.reputation.ReputationCalculator} before
     * this method is called; no math is performed here.</p>
     *
     * <p>Designed for use by
     * {@link com.ctrlwe.quaero.reputation.service.ReputationService} only.
     * No other module or service may call this method to modify XP or
     * credibility.</p>
     *
     * @param userId                 the ID of the user whose progression to update
     * @param newTotalXp             the new cumulative XP total
     * @param newCredibility         the new credibility value
     * @param newCompletedInvestigations the new completed-investigation count
     * @param newSuccessfulSubmissions   the new successful-submission count
     * @throws com.ctrlwe.quaero.exception.ResourceNotFoundException
     *         if no user with the given ID exists
     */
    void updateProgressionFields(Long userId,
                                 int newTotalXp,
                                 BigDecimal newCredibility,
                                 int newCompletedInvestigations,
                                 int newSuccessfulSubmissions);
}

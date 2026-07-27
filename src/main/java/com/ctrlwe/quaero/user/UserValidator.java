package com.ctrlwe.quaero.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Validates user-related operations that require database-backed
 * uniqueness checks.
 *
 * <p>Unlike the {@link com.ctrlwe.quaero.auth.AuthValidator} which
 * performs only stateless validation, this component performs
 * <em>stateful</em> validation against the {@link UserRepository}
 * to enforce uniqueness constraints on email and username.</p>
 *
 * <p>This validator is called by the service layer before persisting
 * a new user. If a duplicate is detected, a
 * {@link UserAlreadyExistsException} is thrown, which the
 * {@link com.ctrlwe.quaero.exception.GlobalExceptionHandler} maps
 * to HTTP 409.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    /**
     * Validates that the given email is not already in use.
     *
     * @param email the email address to check
     * @throws UserAlreadyExistsException if a user with this email
     *         already exists
     */
    public void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(
                    "A user with email '" + email + "' already exists");
        }
    }

    /**
     * Validates that the given username is not already in use.
     *
     * @param username the username to check
     * @throws UserAlreadyExistsException if a user with this username
     *         already exists
     */
    public void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(
                    "A user with username '" + username + "' already exists");
        }
    }
}

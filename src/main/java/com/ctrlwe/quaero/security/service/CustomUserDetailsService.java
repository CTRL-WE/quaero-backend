package com.ctrlwe.quaero.security.service;

import com.ctrlwe.quaero.user.User;
import com.ctrlwe.quaero.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security {@link UserDetailsService} implementation for the
 * Quaero platform.
 *
 * <p>Bridges Spring Security's authentication infrastructure with
 * the Quaero {@link User} entity by loading a user from the
 * {@link UserRepository} and wrapping it in a
 * {@link CustomUserPrincipal}.</p>
 *
 * <p>The {@link #loadUserByUsername(String)} method looks up the user
 * by <strong>username</strong> (the value stored as the JWT {@code sub}
 * claim) via {@link UserRepository#findByUsername(String)}.
 * Login uses email as the credential identifier in
 * {@link com.ctrlwe.quaero.auth.AuthServiceImpl}, but the token subject
 * is set to the username, so this service must query by username.</p>
 *
 * <p>This service is registered as a Spring bean and can be
 * auto-detected by Spring Security or injected into the
 * {@code JwtAuthenticationFilter} when the filter is updated to
 * use a full {@link UserDetails} principal.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by their email address and returns a Spring Security
     * {@link UserDetails} representation.
     *
     * <p>The returned {@link CustomUserPrincipal} carries:</p>
     * <ul>
     *   <li>The user's hashed password for credential verification</li>
     *   <li>A {@code ROLE_}-prefixed authority derived from the user's
     *       {@link com.ctrlwe.quaero.user.Role} (e.g. {@code ROLE_USER},
     *       {@code ROLE_MODERATOR}, {@code ROLE_ADMIN})</li>
     *   <li>Account status flags derived from
     *       {@link com.ctrlwe.quaero.user.AccountStatus}</li>
     *   <li>Access to the underlying {@link User} entity via
     *       {@link CustomUserPrincipal#getUser()}</li>
     * </ul>
     *
     * @param username the user's email address (used as the login
     *                 identifier throughout the Quaero platform)
     * @return a fully populated {@link CustomUserPrincipal}
     * @throws UsernameNotFoundException if no user with the given
     *         email exists in the database
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername called with: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException(
                            "User not found with username: " + username);
                });

        log.debug("User loaded successfully: id={}, username={}", user.getId(), user.getUsername());

        return new CustomUserPrincipal(user);
    }
}

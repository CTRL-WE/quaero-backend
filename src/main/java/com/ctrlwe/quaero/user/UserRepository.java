package com.ctrlwe.quaero.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>Provides the standard CRUD operations inherited from
 * {@link JpaRepository} plus custom query methods for looking
 * up users by their unique identifiers (email, username).</p>
 *
 * <p><strong>Cross-module access rule:</strong> No module outside
 * {@code com.ctrlwe.quaero.user} may inject or call this
 * repository directly. Downstream modules must use
 * {@link UserService} exclusively.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for (case-sensitive)
     * @return an {@link Optional} containing the user if found,
     *         or empty if no user has the given email
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for (case-sensitive)
     * @return an {@link Optional} containing the user if found,
     *         or empty if no user has the given username
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email the email to check
     * @return {@code true} if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether a user with the given username already exists.
     *
     * @param username the username to check
     * @return {@code true} if a user with this username exists
     */
    boolean existsByUsername(String username);
}

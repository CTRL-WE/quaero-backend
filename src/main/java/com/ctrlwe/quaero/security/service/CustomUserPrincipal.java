package com.ctrlwe.quaero.security.service;

import com.ctrlwe.quaero.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security {@link UserDetails} implementation that wraps the
 * existing {@link User} entity.
 *
 * <p>This class acts as a thin adapter between the Quaero domain model
 * and Spring Security's authentication framework. It delegates all
 * identity and credential queries to the underlying {@link User} entity
 * without duplicating any of its fields.</p>
 *
 * <p>The user's {@link com.ctrlwe.quaero.user.Role} is mapped to a
 * Spring Security {@link GrantedAuthority} using the {@code ROLE_}
 * prefix convention (e.g. {@code Role.USER} → {@code ROLE_USER}).
 * This enables standard method-security and URL-based authorisation
 * rules such as {@code hasRole("USER")} or
 * {@code hasAuthority("ROLE_ADMIN")}.</p>
 *
 * <p>Account status checks ({@link #isAccountNonLocked()},
 * {@link #isEnabled()}) are derived from
 * {@link com.ctrlwe.quaero.user.AccountStatus}:
 * <ul>
 *   <li>{@code ACTIVE} → enabled and non-locked</li>
 *   <li>{@code SUSPENDED} → locked (not enabled for login)</li>
 *   <li>{@code DELETED} → disabled</li>
 * </ul></p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public class CustomUserPrincipal implements UserDetails {

    private final User user;

    /**
     * Constructs a new {@code CustomUserPrincipal} wrapping the
     * supplied {@link User} entity.
     *
     * @param user the domain user entity (must not be {@code null})
     */
    public CustomUserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Returns the underlying {@link User} entity.
     *
     * <p>This accessor allows downstream components (e.g. controllers,
     * services) to extract the numeric user ID or other domain fields
     * from the security principal without an additional repository
     * lookup.</p>
     *
     * @return the wrapped user entity
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the user's numeric ID.
     *
     * <p>Convenience method for controllers that need the user ID
     * from the security context (e.g. {@code resolveCurrentUserId()}).</p>
     *
     * @return the user's database ID
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Returns the authorities granted to the user.
     *
     * <p>Maps the user's {@link com.ctrlwe.quaero.user.Role} to a
     * single {@link SimpleGrantedAuthority} with the {@code ROLE_}
     * prefix (e.g. {@code ROLE_USER}, {@code ROLE_MODERATOR},
     * {@code ROLE_ADMIN}).</p>
     *
     * @return a singleton list containing the user's role authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    /**
     * Returns the BCrypt-hashed password stored in the user entity.
     *
     * @return the hashed password
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the user's username
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * <p>Account expiration is not currently modelled in the Quaero
     * platform. Always returns {@code true}.</p>
     *
     * @return {@code true} (accounts never expire)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is locked.
     *
     * <p>An account is considered locked when its status is
     * {@link com.ctrlwe.quaero.user.AccountStatus#SUSPENDED}.</p>
     *
     * @return {@code true} if the account is not suspended
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountStatus() != com.ctrlwe.quaero.user.AccountStatus.SUSPENDED;
    }

    /**
     * Indicates whether the user's credentials have expired.
     *
     * <p>Credential expiration is not currently modelled in the Quaero
     * platform. Always returns {@code true}.</p>
     *
     * @return {@code true} (credentials never expire)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user account is enabled.
     *
     * <p>An account is considered disabled when its status is
     * {@link com.ctrlwe.quaero.user.AccountStatus#DELETED}.</p>
     *
     * @return {@code true} if the account is not deleted
     */
    @Override
    public boolean isEnabled() {
        return user.getAccountStatus() != com.ctrlwe.quaero.user.AccountStatus.DELETED;
    }
}

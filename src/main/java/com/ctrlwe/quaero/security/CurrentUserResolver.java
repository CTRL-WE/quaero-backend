package com.ctrlwe.quaero.security;

import com.ctrlwe.quaero.security.service.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Shared utility component that resolves the numeric database ID of the
 * currently authenticated user from the Spring Security context.
 *
 * <p>This is the <strong>single canonical implementation</strong> of
 * {@code resolveCurrentUserId()} for the platform. All controllers must
 * inject and call this component instead of duplicating the lookup logic.</p>
 *
 * <p>Implementation: the JWT authentication filter sets a
 * {@link CustomUserPrincipal} as the authentication principal after
 * validating the token. {@link CustomUserPrincipal} already wraps the
 * full {@link com.ctrlwe.quaero.user.User} entity (loaded by
 * {@link com.ctrlwe.quaero.security.service.CustomUserDetailsService}),
 * so the user ID is available via {@link CustomUserPrincipal#getId()}
 * with zero additional database queries.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class CurrentUserResolver {

    /**
     * Returns the numeric database ID of the currently authenticated user.
     *
     * <p>This method must only be called from within a request that has
     * passed JWT authentication. Spring Security's {@code anyRequest().authenticated()}
     * rule guarantees this for all non-public endpoints, so calling this
     * method from any protected controller is always safe.</p>
     *
     * @return the authenticated user's database ID (never {@code null}
     *         on authenticated requests)
     * @throws IllegalStateException if the security context contains no
     *         authenticated principal — this should never happen on a
     *         protected endpoint and indicates a security configuration bug
     */
    public Long resolveCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || !(auth.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new IllegalStateException(
                    "No authenticated CustomUserPrincipal in SecurityContext. " +
                    "This endpoint must be protected by JwtAuthenticationFilter.");
        }

        return principal.getId();
    }
}

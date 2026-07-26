package com.ctrlwe.quaero.security;

import com.ctrlwe.quaero.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter for the Quaero platform.
 *
 * <p>Executes once per request, before the default
 * {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}.
 * Reads the {@code Authorization: Bearer <token>} header, validates the token
 * using {@link JwtService}, and — if valid — populates the
 * {@link SecurityContextHolder} so that Spring Security's
 * {@code anyRequest().authenticated()} rule is satisfied for the remainder
 * of the filter chain.</p>
 *
 * <p>If the header is absent, malformed, or the token fails validation,
 * the filter passes the request on without setting any authentication.
 * Spring Security's entry point ({@link JwtAuthenticationEntryPoint}) will
 * then return HTTP 401 for protected endpoints.</p>
 *
 * <p><strong>Note on UserDetails:</strong> The User entity and repository are
 * not yet complete (Vishwa's user/ module is in progress). This filter
 * therefore sets an authentication token with no granted authorities and
 * uses the JWT subject (email) as the principal name directly. Once the
 * User/Profile module is available and a {@code UserDetailsService} bean
 * is registered, replace the {@code UsernamePasswordAuthenticationToken}
 * construction below with a proper {@code UserDetails} load.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Processes the incoming request for JWT-based authentication.
     *
     * @param request     the current HTTP request
     * @param response    the current HTTP response
     * @param filterChain the remaining filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extract the JWT from the Authorization header.
        String token = extractTokenFromRequest(request);

        // 2. Validate the token and populate the SecurityContext if valid.
        if (StringUtils.hasText(token) && jwtService.validateAccessToken(token)) {

            // 3. Extract the username (subject claim) from the validated token.
            String username = jwtService.extractUsername(token);

            // 4. Build an authentication token.
            //    TODO: Replace Collections.emptyList() with actual granted authorities
            //          once the User entity and role-based access control are wired in
            //          by the User/Profile module. At that point, load UserDetails via
            //          UserDetailsService and pass userDetails.getAuthorities() here.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.emptyList()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // 5. Populate the SecurityContextHolder so downstream Spring Security
            //    checks (anyRequest().authenticated()) see a valid principal.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT authentication set for user: {}", username);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the raw JWT string from the {@code Authorization} header.
     *
     * @param request the current HTTP request
     * @return the token string, or {@code null} if the header is absent or
     *         does not start with the expected {@code "Bearer "} prefix
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.JWT_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return header.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        return null;
    }
}


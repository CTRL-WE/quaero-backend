package com.ctrlwe.quaero.security;

import com.ctrlwe.quaero.security.jwt.JwtService;
import com.ctrlwe.quaero.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter for the Quaero platform.
 *
 * <p>Executes once per request, before the default
 * {@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}.
 * Reads the {@code Authorization: Bearer <token>} header, validates the token
 * using {@link JwtService}, and — if valid — loads the full
 * {@link UserDetails} via {@link CustomUserDetailsService} and populates the
 * {@link SecurityContextHolder} so that Spring Security's
 * {@code anyRequest().authenticated()} rule is satisfied for the remainder
 * of the filter chain.</p>
 *
 * <p>If the header is absent, malformed, or the token fails validation,
 * the filter passes the request on without setting any authentication.
 * Spring Security's entry point ({@link JwtAuthenticationEntryPoint}) will
 * then return HTTP 401 for protected endpoints.</p>
 *
 * <p>The authenticated principal is a
 * {@link com.ctrlwe.quaero.security.service.CustomUserPrincipal} that
 * carries the user's granted authorities (e.g. {@code ROLE_USER},
 * {@code ROLE_MODERATOR}, {@code ROLE_ADMIN}) and account status flags,
 * enabling both URL-based and method-level authorisation checks
 * downstream.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Processes the incoming request for JWT-based authentication.
     *
     * <ol>
     *   <li>Extracts the JWT from the {@code Authorization} header.</li>
     *   <li>If absent or not prefixed with {@code "Bearer "}, continues
     *       the filter chain without authentication.</li>
     *   <li>Validates the access token via {@link JwtService}.</li>
     *   <li>If invalid, continues the filter chain without
     *       authentication (no exception is thrown).</li>
     *   <li>If valid, extracts the username from the token, loads the
     *       full {@link UserDetails} via {@link CustomUserDetailsService},
     *       builds a {@link UsernamePasswordAuthenticationToken} with
     *       the user's real authorities, and stores it in the
     *       {@link SecurityContextHolder}.</li>
     * </ol>
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
        if (StringUtils.hasText(token)
                && jwtService.validateAccessToken(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 3. Extract the username (subject claim) from the validated token.
            String username = jwtService.extractUsername(token);

            // 4. Load the full UserDetails from the database.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Build an authentication token with real granted authorities.
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. Populate the SecurityContextHolder so downstream Spring Security
            //    checks (anyRequest().authenticated()) see a valid principal.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT authentication set for user: {} with authorities: {}",
                    username, userDetails.getAuthorities());
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


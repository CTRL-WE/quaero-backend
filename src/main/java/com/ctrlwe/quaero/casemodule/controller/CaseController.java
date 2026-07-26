package com.ctrlwe.quaero.casemodule.controller;

import com.ctrlwe.quaero.casemodule.dto.CaseBriefResponse;
import com.ctrlwe.quaero.casemodule.dto.CaseFeedItem;
import com.ctrlwe.quaero.casemodule.service.CaseService;
import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the Case module.
 *
 * <p>Exposes exactly two public endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/cases} — the investigation feed (all published cases)</li>
 *   <li>{@code GET /api/cases/{id}/brief} — the full brief for a single case</li>
 * </ul>
 *
 * <p>Both endpoints require a valid JWT in the {@code Authorization: Bearer <token>}
 * header. Authentication is enforced by the platform-wide
 * {@code SecurityConfig} ({@code anyRequest().authenticated()}) and the
 * {@code JwtAuthenticationFilter} — no security code lives in this controller.</p>
 *
 * <p><strong>No other endpoint exists or will be added here.</strong>
 * {@code CaseService#getFullContext(Long)} has no HTTP path and must
 * never be wired to any controller method.</p>
 *
 * <p>The {@code userId} passed to {@link CaseService#getFeed(Long)} is
 * resolved from the Spring Security context rather than accepted as a
 * request parameter, so that a user can never request feed data for a
 * different user's completion state.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Tag(name = "Cases", description = "Investigation case feed and brief endpoints.")
@SecurityRequirement(name = "bearerAuth")
public class CaseController {

    private final CaseService caseService;

    /**
     * Returns all published cases as a feed of {@link CaseFeedItem} objects.
     *
     * <p>Each item contains a short evidence teaser (≤ 150 characters) and a
     * per-user completion flag. Cases are ordered newest-first. Draft cases
     * are never included.</p>
     *
     * @return HTTP 200 with the list of feed items (may be empty)
     */
    @GetMapping
    @Operation(
        summary = "Get investigation feed",
        description = "Returns all PUBLISHED cases as feed items. Each item includes " +
                      "the case ID, claim, a 150-character evidence teaser, and a flag " +
                      "indicating whether the authenticated user has already submitted " +
                      "for that case. Draft cases are never included."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Feed returned successfully (may be an empty list).",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CaseFeedItem.class))
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<List<CaseFeedItem>>> getFeed() {
        Long userId = resolveCurrentUserId();
        log.debug("GET /api/cases — userId={}", userId);

        List<CaseFeedItem> feed = caseService.getFeed(userId);

        return ResponseEntity.ok(
                ApiResponse.success(feed, "Feed retrieved successfully"));
    }

    /**
     * Returns the full public Brief for a single published case.
     *
     * <p>The brief contains only the case ID, claim, and the full
     * public evidence summary. No internal field is present in the response.</p>
     *
     * @param id the case identifier from the URL path
     * @return HTTP 200 with the brief, or HTTP 404 if the case does not exist
     */
    @GetMapping("/{id}/brief")
    @Operation(
        summary = "Get case brief",
        description = "Returns the full public brief for a single PUBLISHED case: " +
                      "the case ID, the claim, and the complete public evidence summary. " +
                      "No internal content (ground truth, hints, etc.) is included. " +
                      "Returns 404 if the case ID does not exist."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Brief returned successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CaseBriefResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Case not found — no case with the supplied ID exists.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<CaseBriefResponse>> getBrief(@PathVariable Long id) {
        log.debug("GET /api/cases/{}/brief", id);

        CaseBriefResponse brief = caseService.getBrief(id);

        return ResponseEntity.ok(
                ApiResponse.success(brief, "Case brief retrieved successfully"));
    }

    // ----------------------------------------------------------------
    // Utility
    // ----------------------------------------------------------------

    /**
     * Resolves the authenticated user's ID from the Spring Security context.
     *
     * <p>The JWT filter populates the {@code SecurityContext} before this
     * controller method is reached. The principal name is used as the user
     * identifier until the User/Profile module provides a lookup mechanism.</p>
     *
     * <p>If the security context holds no authentication (which should be
     * impossible given the {@code anyRequest().authenticated()} rule in
     * {@code SecurityConfig}), a sentinel value of {@code -1L} is returned
     * so that the service can still function without a NullPointerException.
     * The {@code alreadyCompleted} stub will return {@code false} in that
     * case regardless.</p>
     *
     * <p>TODO: Once the User/Profile module is available and the JWT filter
     * populates the security context with a real {@code UserDetails} object,
     * update this method to extract the numeric user ID from the principal
     * rather than relying on the username string. The service signature
     * already accepts {@code Long userId}, so no downstream change is needed.</p>
     *
     * @return the current user's ID, or {@code -1L} as a safe sentinel
     */
    private Long resolveCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("resolveCurrentUserId: no authenticated principal in SecurityContext");
            return -1L;
        }
        // TODO: Replace with UserService#findByUsername(principal.getName()).getId()
        //       once the User/Profile module exposes that lookup through its service
        //       interface. Until then, return -1L as a safe sentinel; the
        //       alreadyCompleted field will remain false regardless.
        return -1L;
    }
}

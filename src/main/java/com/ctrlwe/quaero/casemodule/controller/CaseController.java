package com.ctrlwe.quaero.casemodule.controller;

import com.ctrlwe.quaero.casemodule.dto.CaseBriefResponse;
import com.ctrlwe.quaero.casemodule.dto.CaseFeedItem;
import com.ctrlwe.quaero.casemodule.service.CaseService;
import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.common.response.ApiResponse;
import com.ctrlwe.quaero.security.CurrentUserResolver;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the Case module — the Investigation Feed and
 * Observe endpoints.
 *
 * <p>Exposes exactly two public endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/cases} — returns the Investigation Feed:
 *       all published Investigation Challenges as scrollable cards.</li>
 *   <li>{@code GET /api/cases/{id}/brief} — returns the Observe
 *       screen for a single Investigation Challenge: the full claim,
 *       public evidence, and complete Original Post metadata.</li>
 * </ul>
 *
 * <p>Both endpoints require a valid JWT. No internal case data
 * (ground truth, evaluation hints, etc.) is ever returned.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
@Tag(name = "Investigation Feed",
     description = "Investigation Challenge feed and Observe screen endpoints. " +
                   "Each challenge presents a real-world claim as a social-media " +
                   "post for users to investigate.")
@SecurityRequirement(name = "bearerAuth")
public class CaseController {

    private final CaseService caseService;
    private final CurrentUserResolver currentUserResolver;

    /**
     * Returns the Investigation Feed — all published Investigation
     * Challenges as a list of {@link CaseFeedItem} cards.
     *
     * <p>Each card contains: the claim, an evidence teaser, platform
     * metadata, engagement metrics, a difficulty badge, and a per-user
     * completion flag. Cases are ordered newest-first. Draft cases
     * are never included.</p>
     *
     * @return HTTP 200 with the list of feed items (may be empty)
     */
    @GetMapping
    @Operation(
        summary = "Get Investigation Feed",
        description = "Returns all published Investigation Challenges as " +
                      "scrollable feed cards. Each card includes the claim, " +
                      "a 150-character evidence teaser, platform and media type, " +
                      "engagement metrics (likes, comments, shares), a " +
                      "verification difficulty badge, category tag, and a flag " +
                      "indicating whether the authenticated user has already " +
                      "submitted for that challenge. Draft cases are never " +
                      "included. Ordered newest-first."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Investigation Feed returned successfully (may be empty).",
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
                ApiResponse.success(feed, "Investigation Feed retrieved successfully"));
    }

    /**
     * Returns the Observe screen for a single Investigation Challenge.
     *
     * <p>The Observe screen contains the full claim, untruncated public
     * evidence summary, and the complete Original Post presentation
     * metadata (platform, poster, caption, full-size media, engagement
     * metrics, publish date, difficulty, category). No internal field
     * is present in the response.</p>
     *
     * @param id the Investigation Challenge identifier from the URL path
     * @return HTTP 200 with the Observe data, or HTTP 404 if not found
     */
    @GetMapping("/{id}/brief")
    @Operation(
        summary = "Get Investigation Challenge — Observe",
        description = "Returns the full Observe screen for a single " +
                      "Investigation Challenge. Includes the claim, the " +
                      "complete public evidence summary, and the full " +
                      "Original Post metadata: platform, poster handle, " +
                      "caption, media (thumbnail + full-size URL), " +
                      "engagement metrics, publish date, difficulty level, " +
                      "and category. No internal content (ground truth, " +
                      "hints, etc.) is included. Returns 404 if the " +
                      "challenge ID does not exist."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Observe data returned successfully.",
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
            description = "Investigation Challenge not found.",
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
                ApiResponse.success(brief, "Investigation Challenge retrieved successfully"));
    }

    // ----------------------------------------------------------------
    // Utility — delegated to shared CurrentUserResolver
    // ----------------------------------------------------------------

    private Long resolveCurrentUserId() {
        return currentUserResolver.resolveCurrentUserId();
    }
}

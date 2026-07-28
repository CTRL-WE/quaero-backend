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
 * REST controller for the Case module.
 *
 * <p>Exposes exactly two public endpoints:</p>
 * <ul>
 *   <li>{@code GET /api/cases} — returns all published cases as a feed.</li>
 *   <li>{@code GET /api/cases/{id}/brief} — returns the public brief for
 *       a single published case.</li>
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
@Tag(name = "Cases", description = "Investigation case feed and brief endpoints.")
@SecurityRequirement(name = "bearerAuth")
public class CaseController {

    private final CaseService caseService;
    private final CurrentUserResolver currentUserResolver;

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
    // Utility — delegated to shared CurrentUserResolver
    // ----------------------------------------------------------------

    private Long resolveCurrentUserId() {
        return currentUserResolver.resolveCurrentUserId();
    }
}

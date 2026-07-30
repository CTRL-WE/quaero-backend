package com.ctrlwe.quaero.reputation.controller;

import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.common.response.ApiResponse;
import com.ctrlwe.quaero.reputation.dto.LeaderboardEntryResponse;
import com.ctrlwe.quaero.reputation.service.ReputationService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing the public platform leaderboard.
 *
 * <p>Exposes exactly one endpoint:</p>
 * <ul>
 *   <li>{@code GET /api/leaderboard} — returns all users ordered by credibility
 *       DESC, then XP DESC.</li>
 * </ul>
 *
 * <p>Requires a valid JWT in the {@code Authorization: Bearer &lt;token&gt;} header.
 * Authentication is enforced by the platform-wide {@code SecurityConfig} —
 * no security code lives in this controller.</p>
 *
 * <p>No business logic is performed here. All logic is delegated to
 * {@link ReputationService}. Responses are wrapped in the standard
 * {@link ApiResponse} envelope ({@code success}, {@code status}, {@code message},
 * {@code data}).</p>
 *
 * <p>No pagination, no filtering — the full ordered list is returned in one
 * response, per the frozen specification.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard",
        description = "Platform leaderboard showing users ranked by credibility and XP.")
@SecurityRequirement(name = "bearerAuth")
public class LeaderboardController {

    private final ReputationService reputationService;

    /**
     * Returns the full platform leaderboard.
     *
     * <p>The list is ordered by credibility DESC (users with no submissions
     * sort to the bottom), then by total XP DESC as a tiebreaker. Position is
     * 1-indexed within the response.</p>
     *
     * @return HTTP 200 with the full ordered leaderboard list
     */
    @GetMapping
    @Operation(
        summary = "Get the platform leaderboard",
        description = "Returns all users ordered by credibility DESC (nulls last), then " +
                "total XP DESC. Position is 1-indexed. JWT required. " +
                "No pagination — the full list is returned in one response."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Leaderboard retrieved successfully.",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = LeaderboardEntryResponse.class))
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
    public ResponseEntity<ApiResponse<List<LeaderboardEntryResponse>>> getLeaderboard() {
        log.debug("GET /api/leaderboard called");

        List<LeaderboardEntryResponse> leaderboard = reputationService.getLeaderboard();

        return ResponseEntity.ok(
                ApiResponse.success(leaderboard, "Leaderboard retrieved successfully"));
    }
}

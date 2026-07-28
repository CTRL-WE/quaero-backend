package com.ctrlwe.quaero.investigation.controller;

import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.common.response.ApiResponse;
import com.ctrlwe.quaero.investigation.dto.ChatMessageRequest;
import com.ctrlwe.quaero.investigation.dto.ChatMessageResponse;
import com.ctrlwe.quaero.investigation.dto.SessionStatusResponse;
import com.ctrlwe.quaero.investigation.service.InvestigationService;
import com.ctrlwe.quaero.security.CurrentUserResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Investigation module.
 *
 * <p>Exposes exactly two endpoints:</p>
 * <ul>
 *   <li>{@code POST /api/investigations/{caseId}/messages} — send a message
 *       to the AI Mentor and receive a Socratic reply.</li>
 *   <li>{@code GET /api/investigations/{caseId}/status} — read the current
 *       state of the user's investigation session for a case.</li>
 * </ul>
 *
 * <p>Both endpoints require a valid JWT in the
 * {@code Authorization: Bearer <token>} header. Authentication is enforced
 * by the platform-wide {@code SecurityConfig} ({@code anyRequest().authenticated()})
 * and the {@code JwtAuthenticationFilter} — no security code lives here.</p>
 *
 * <p>The {@code userId} is resolved from the Spring Security context (same
 * pattern as {@code CaseController}) and never accepted from the request body
 * or query parameters. This prevents a user from posting into or reading
 * another user's session.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/investigations")
@RequiredArgsConstructor
@Tag(
    name = "Investigations",
    description = "AI Mentor conversation endpoints. The AI Mentor never states a verdict — " +
                  "it guides users through Socratic questioning only."
)
@SecurityRequirement(name = "bearerAuth")
public class InvestigationController {

    private final InvestigationService investigationService;
    private final CurrentUserResolver currentUserResolver;

    // ----------------------------------------------------------------
    // POST /api/investigations/{caseId}/messages
    // ----------------------------------------------------------------

    /**
     * Sends a user message to the AI Mentor and returns a Socratic reply.
     *
     * <p>If the user has no active session for this case, one is created
     * transparently. If the session is already SUBMITTED, HTTP 409 is
     * returned. If the case does not exist or is not published, HTTP 404
     * is returned.</p>
     *
     * @param caseId  the ID of the case being investigated (from path)
     * @param request the message body (must not be blank)
     * @return the AI Mentor's reply, current turn count, and nudgeSubmission flag
     */
    @PostMapping("/{caseId}/messages")
    @Operation(
        summary = "Send a message to the AI Mentor",
        description = "Persists the user's message, sends it to the AI Mentor, " +
                      "and returns a Socratic reply. A new session is created transparently " +
                      "if one does not exist. The AI will never state whether the claim is " +
                      "true or false — it guides the user's reasoning through questions only. " +
                      "\n\n**nudgeSubmission** in the response is always false in " +
                      "Investigation v1 — it is a placeholder for a future signal that will " +
                      "tell the frontend to prompt the user to submit their verdict. " +
                      "Do NOT wire frontend logic to this field yet."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "AI Mentor replied successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ChatMessageResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "message field was blank.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
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
            description = "Case not found or not published.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "The investigation session has already been submitted — " +
                          "no further messages can be added.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<ChatMessageResponse>> postMessage(
            @PathVariable Long caseId,
            @Valid @RequestBody ChatMessageRequest request) {

        Long userId = resolveCurrentUserId();
        log.debug("POST /api/investigations/{}/messages — userId={}", caseId, userId);

        // Combine path variable into the request record.
        // ChatMessageRequest carries caseId to keep the service signature clean,
        // but the authoritative source is always the path variable.
        ChatMessageResponse response = investigationService.postMessage(
                userId, caseId, request.message());

        return ResponseEntity.ok(
                ApiResponse.success(response, "AI Mentor response returned successfully."));
    }

    // ----------------------------------------------------------------
    // GET /api/investigations/{caseId}/status
    // ----------------------------------------------------------------

    /**
     * Returns the current investigation session state for the authenticated
     * user on the specified case.
     *
     * <p>Returns {@code sessionExists = false} with a null status and zero
     * turn count if the user has not yet started investigating this case.
     * This endpoint exists today so that the future Submission module can
     * call it as an existence check without coupling to Investigation internals.</p>
     *
     * @param caseId the ID of the case
     * @return the session's lifecycle status and turn count
     */
    @GetMapping("/{caseId}/status")
    @Operation(
        summary = "Get investigation session status",
        description = "Returns the current state of the authenticated user's investigation " +
                      "session for the given case. If no session exists yet " +
                      "(the user has not started investigating), sessionExists will be false " +
                      "and status will be null. This endpoint is intentionally minimal — " +
                      "it exists as an existence-check surface for the future Submission module."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Status returned successfully (sessionExists may be false " +
                          "if the user has not yet started).",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SessionStatusResponse.class)
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
    public ResponseEntity<ApiResponse<SessionStatusResponse>> getSessionStatus(
            @PathVariable Long caseId) {

        Long userId = resolveCurrentUserId();
        log.debug("GET /api/investigations/{}/status — userId={}", caseId, userId);

        SessionStatusResponse status = investigationService.getSessionStatus(userId, caseId);

        return ResponseEntity.ok(
                ApiResponse.success(status, "Session status retrieved successfully."));
    }

    // ----------------------------------------------------------------
    // Utility — delegated to shared CurrentUserResolver
    // ----------------------------------------------------------------

    private Long resolveCurrentUserId() {
        return currentUserResolver.resolveCurrentUserId();
    }
}

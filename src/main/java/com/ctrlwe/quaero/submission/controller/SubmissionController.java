package com.ctrlwe.quaero.submission.controller;

import com.ctrlwe.quaero.common.response.ApiErrorResponse;
import com.ctrlwe.quaero.common.response.ApiResponse;
import com.ctrlwe.quaero.security.CurrentUserResolver;
import com.ctrlwe.quaero.submission.dto.CreateSubmissionRequest;
import com.ctrlwe.quaero.submission.dto.SubmissionResponse;
import com.ctrlwe.quaero.submission.dto.SubmissionSummaryResponse;
import com.ctrlwe.quaero.submission.dto.UpdateSubmissionRequest;
import com.ctrlwe.quaero.submission.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the Submission module.
 *
 * <p>Exposes the following endpoints under {@code /api/submissions}:</p>
 * <ul>
 *   <li>{@code POST   /api/submissions/cases/{caseId}}   — create a new submission</li>
 *   <li>{@code PUT    /api/submissions/{submissionId}}    — update a submission (owner only)</li>
 *   <li>{@code GET    /api/submissions/{submissionId}}    — retrieve a single submission</li>
 *   <li>{@code GET    /api/submissions/cases/{caseId}}    — list submissions for a case</li>
 *   <li>{@code DELETE /api/submissions/{submissionId}}    — delete a submission (owner only)</li>
 * </ul>
 *
 * <p>All mutating endpoints (POST, PUT, DELETE) require a valid JWT in the
 * {@code Authorization: Bearer <token>} header. Authentication is enforced
 * by the platform-wide {@code SecurityConfig} — no security code lives in
 * this controller.</p>
 *
 * <p>No business logic is performed in this controller. All operations are
 * delegated to {@link SubmissionService}. Responses are wrapped in
 * {@link ApiResponse} for consistency with the rest of the platform.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Submission", description = "Evidence submission management endpoints.")
@SecurityRequirement(name = "bearerAuth")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final CurrentUserResolver currentUserResolver;

    /**
     * Creates a new evidence submission for the specified case.
     *
     * <p>The authenticated user is resolved from the Spring Security
     * context — the user ID is never accepted from the request body.</p>
     *
     * @param caseId  the case identifier from the URL path
     * @param request the creation payload
     * @return HTTP 201 with the created submission
     */
    @PostMapping("/cases/{caseId}")
    @Operation(
        summary = "Create a new submission",
        description = "Creates a new evidence submission for the specified case. " +
                      "The authenticated user is automatically set as the submission owner. " +
                      "The submission starts in PENDING status."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Submission created successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SubmissionResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation failed on one or more fields.",
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
            description = "User or case not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<SubmissionResponse>> createSubmission(
            @PathVariable Long caseId,
            @Valid @RequestBody CreateSubmissionRequest request) {

        Long userId = resolveCurrentUserId();
        log.debug("POST /api/submissions/cases/{} — userId={}", caseId, userId);

        SubmissionResponse response = submissionService.createSubmission(userId, caseId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Submission created successfully", 201));
    }

    /**
     * Updates an existing submission's mutable fields.
     *
     * <p>Only the submission owner may perform this operation;
     * ownership is enforced by the service layer.</p>
     *
     * @param submissionId the submission identifier from the URL path
     * @param request      the update payload
     * @return HTTP 200 with the updated submission
     */
    @PutMapping("/{submissionId}")
    @Operation(
        summary = "Update a submission",
        description = "Updates the mutable fields of an existing submission. " +
                      "Only the submission owner may perform this operation. " +
                      "Status cannot be changed through this endpoint."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Submission updated successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SubmissionResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation failed on one or more fields.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT, or user is not the submission owner.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Submission not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<SubmissionResponse>> updateSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody UpdateSubmissionRequest request) {

        Long userId = resolveCurrentUserId();
        log.debug("PUT /api/submissions/{} — userId={}", submissionId, userId);

        SubmissionResponse response = submissionService.updateSubmission(submissionId, userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Submission updated successfully"));
    }

    /**
     * Retrieves a single submission by its ID.
     *
     * @param submissionId the submission identifier from the URL path
     * @return HTTP 200 with the submission details
     */
    @GetMapping("/{submissionId}")
    @Operation(
        summary = "Get a submission",
        description = "Retrieves the full details of a single submission by its ID."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Submission returned successfully.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SubmissionResponse.class)
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
            description = "Submission not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmission(
            @PathVariable Long submissionId) {

        log.debug("GET /api/submissions/{}", submissionId);

        SubmissionResponse response = submissionService.getSubmission(submissionId);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Submission retrieved successfully"));
    }

    /**
     * Retrieves all submissions for the specified case, ordered newest first.
     *
     * @param caseId the case identifier from the URL path
     * @return HTTP 200 with the list of submission summaries (may be empty)
     */
    @GetMapping("/cases/{caseId}")
    @Operation(
        summary = "Get submissions for a case",
        description = "Retrieves all submissions for the specified case, " +
                      "ordered by creation date descending (newest first). " +
                      "Returns lightweight summary objects."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Submissions returned successfully (may be an empty list).",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(
                    schema = @Schema(implementation = SubmissionSummaryResponse.class)
                )
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
    public ResponseEntity<ApiResponse<List<SubmissionSummaryResponse>>> getSubmissionsForCase(
            @PathVariable Long caseId) {

        log.debug("GET /api/submissions/cases/{}", caseId);

        List<SubmissionSummaryResponse> submissions = submissionService.getSubmissionsForCase(caseId);

        return ResponseEntity.ok(
                ApiResponse.success(submissions, "Submissions retrieved successfully"));
    }

    /**
     * Deletes a submission by its ID.
     *
     * <p>Only the submission owner may perform this operation;
     * ownership is enforced by the service layer.</p>
     *
     * @param submissionId the submission identifier from the URL path
     * @return HTTP 204 with no content
     */
    @DeleteMapping("/{submissionId}")
    @Operation(
        summary = "Delete a submission",
        description = "Deletes an existing submission. Only the submission owner " +
                      "may perform this operation."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Submission deleted successfully.",
            content = @Content
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Missing or invalid JWT, or user is not the submission owner.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Submission not found.",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long submissionId) {

        Long userId = resolveCurrentUserId();
        log.debug("DELETE /api/submissions/{} — userId={}", submissionId, userId);

        submissionService.deleteSubmission(submissionId, userId);

        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------
    // Utility — delegated to shared CurrentUserResolver
    // ----------------------------------------------------------------

    /**
     * Delegates to the shared {@link CurrentUserResolver} to extract the
     * authenticated user's numeric ID from the security context.
     *
     * @return the current user's database ID
     */
    private Long resolveCurrentUserId() {
        return currentUserResolver.resolveCurrentUserId();
    }
}

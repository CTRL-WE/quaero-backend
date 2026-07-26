package com.ctrlwe.quaero.casemodule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Public DTO representing a single case entry on the Feed screen.
 *
 * <p>This class carries only the fields a user is permitted to see
 * <em>before and during</em> an investigation:</p>
 * <ul>
 *   <li>{@link #id} — the case identifier (needed by the client to
 *       navigate to the Brief screen)</li>
 *   <li>{@link #claim} — the investigable claim</li>
 *   <li>{@link #evidenceTeaser} — a truncated preview of the public
 *       evidence summary (first 150 characters, followed by {@code "..."}
 *       if the original is longer). The truncation is applied in
 *       {@code CaseServiceImpl#getFeed} at the service layer.</li>
 *   <li>{@link #alreadyCompleted} — whether the requesting user has
 *       already submitted their reasoning for this case</li>
 * </ul>
 *
 * <p><strong>Field set is final.</strong> No field from the internal shape
 * ({@code groundTruth}, {@code groundTruthExplanation}, {@code trustedReferences},
 * {@code investigationHints}, {@code learningSummary}) may ever be added
 * to this class.</p>
 *
 * <p>This is one of the two classes that appear in the generated OpenAPI
 * spec and form the binding API contract that the frontend Feed screen is
 * built against.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@Schema(
    name = "CaseFeedItem",
    description = "A single case entry shown on the investigation feed. " +
                  "Contains only publicly safe fields — no internal case content."
)
public class CaseFeedItem {

    /**
     * Unique case identifier. Used by the client to fetch the full Brief.
     */
    @Schema(
        description = "Unique case identifier.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final Long id;

    /**
     * The real-world claim this case asks the user to investigate.
     */
    @Schema(
        description = "The investigable claim presented to the user.",
        example = "A viral video shows a foreign leader signing a document that contradicts their public statements.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String claim;

    /**
     * A truncated teaser of the public evidence summary (≤ 150 characters,
     * with {@code "..."} appended if the original was longer).
     * The full summary is available on the Brief endpoint.
     */
    @Schema(
        description = "Truncated preview of the public evidence summary " +
                      "(max 150 characters, ellipsis-terminated if truncated). " +
                      "Fetch GET /api/cases/{id}/brief for the full text.",
        example = "Multiple news outlets reported the video on the same day. Reverse image search tools are publicly available...",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String evidenceTeaser;

    /**
     * Whether the requesting user has already submitted their reasoning
     * for this case.
     *
     * <p>Completed cases are still shown on the Feed (they are never hidden),
     * but this flag allows the frontend to render a visual indicator
     * (e.g. a checkmark) so the user knows they have already investigated it.</p>
     *
     * <!-- TODO: Wire to Submission module.
     *      Currently stubbed as {@code false} for every case because the
     *      Submission module does not exist yet. Once Submission is built,
     *      replace this with a call to SubmissionService#hasUserSubmitted(userId, caseId)
     *      (through its service interface — never by reaching into its repository).
     *      The method signature getFeed(Long userId) already receives userId
     *      so no CaseController changes are needed at that point. -->
     */
    @Schema(
        description = "True if the authenticated user has already submitted a " +
                      "reasoning verdict for this case. Always false until the " +
                      "Submission module is wired in.",
        example = "false",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final boolean alreadyCompleted;
}

package com.ctrlwe.quaero.casemodule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Public DTO representing the full Brief shown to a user who has selected
 * a case to investigate.
 *
 * <p>This class carries exactly three fields and no others, ever:</p>
 * <ul>
 *   <li>{@link #id} — the case identifier</li>
 *   <li>{@link #claim} — the investigable claim (same as on the Feed card)</li>
 *   <li>{@link #publicEvidenceSummary} — the <em>full</em> public evidence
 *       summary (not truncated, unlike the Feed teaser)</li>
 * </ul>
 *
 * <p><strong>Field set is final and immutable by design.</strong> No field from
 * the internal shape ({@code groundTruth}, {@code groundTruthExplanation},
 * {@code trustedReferences}, {@code investigationHints}, {@code learningSummary})
 * may ever appear in this class under any circumstance. Adding even a single
 * internal field here would break the platform's core pedagogical premise by
 * showing users the answer before they reason toward it themselves.</p>
 *
 * <p>This is one of the two classes that appear in the generated OpenAPI spec
 * and form the binding API contract that the frontend Brief screen is built
 * against.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@Schema(
    name = "CaseBriefResponse",
    description = "The full investigation brief for a single case. " +
                  "Contains only the claim and full public evidence summary — " +
                  "no internal case content of any kind."
)
public class CaseBriefResponse {

    /**
     * Unique case identifier.
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
     * The complete, untruncated public evidence summary.
     * This is the full text the user reads before beginning their investigation.
     * It contains only publicly available evidence — no hints, no ground truth.
     */
    @Schema(
        description = "The full public evidence summary. Contains only publicly available " +
                      "evidence relevant to the claim. No hints, no ground truth, no answer.",
        example = "Multiple international news outlets reported on the video within hours of it surfacing online. " +
                  "Reverse image search tools such as TinEye and Google Images are freely accessible. " +
                  "Several media-literacy organisations have published guides on verifying video authenticity.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String publicEvidenceSummary;
}

package com.ctrlwe.quaero.casemodule.dto;

import com.ctrlwe.quaero.casemodule.entity.Category;
import com.ctrlwe.quaero.casemodule.entity.MediaType;
import com.ctrlwe.quaero.casemodule.entity.Platform;
import com.ctrlwe.quaero.casemodule.entity.VerificationDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Public DTO representing the full Observe screen for a single
 * Investigation Challenge.
 *
 * <p>This class carries the complete public data the user sees before
 * beginning their investigation:</p>
 * <ul>
 *   <li>Investigative content: {@link #id}, {@link #claim},
 *       {@link #publicEvidenceSummary}</li>
 *   <li>Full presentation metadata (V2 "Original Post"):
 *       {@link #platform}, {@link #originalPoster}, {@link #caption},
 *       {@link #thumbnailUrl}, {@link #mediaUrl}, {@link #mediaType},
 *       engagement counts, {@link #verificationDifficulty},
 *       {@link #publishedAt}, {@link #category}</li>
 * </ul>
 *
 * <p>This is where the user first encounters the "Original Post" in
 * full fidelity — the full-size media, caption, poster handle, and
 * post date are all revealed here, not on the Feed card.</p>
 *
 * <p><strong>No field from the internal shape</strong>
 * ({@code groundTruth}, {@code groundTruthExplanation},
 * {@code trustedReferences}, {@code investigationHints},
 * {@code learningSummary}) may ever appear in this class under any
 * circumstance. Adding even a single internal field here would break
 * the platform's core pedagogical premise by showing users the answer
 * before they reason toward it themselves.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@Schema(
    name = "CaseBriefResponse",
    description = "The full Observe screen for a single Investigation Challenge. " +
                  "Contains the claim, full public evidence summary, and the " +
                  "complete Original Post presentation metadata. " +
                  "No internal case content of any kind."
)
public class CaseBriefResponse {

    // ----------------------------------------------------------------
    // Investigative content
    // ----------------------------------------------------------------

    /**
     * Unique case identifier.
     */
    @Schema(
        description = "Unique Investigation Challenge identifier.",
        example = "1",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final Long id;

    /**
     * The real-world claim this Investigation Challenge asks the user
     * to investigate.
     */
    @Schema(
        description = "The investigable claim presented to the user.",
        example = "A viral video shows a foreign leader signing a document " +
                  "that contradicts their public statements.",
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
        example = "Multiple international news outlets reported on the video within hours " +
                  "of it surfacing online. Reverse image search tools such as TinEye and " +
                  "Google Images are freely accessible. Several media-literacy organisations " +
                  "have published guides on verifying video authenticity.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String publicEvidenceSummary;

    // ----------------------------------------------------------------
    // Presentation metadata — full "Original Post" (V2 architecture)
    // ----------------------------------------------------------------

    /**
     * The social media platform the original post came from.
     */
    @Schema(
        description = "Social media platform of the original post. " +
                      "Null for legacy cases created before V2.",
        example = "INSTAGRAM"
    )
    private final Platform platform;

    /**
     * The display name or handle of the original poster as it appeared
     * in the social media post.
     */
    @Schema(
        description = "Display name or handle of the original poster. " +
                      "Null for legacy cases.",
        example = "@breaking_news_daily"
    )
    private final String originalPoster;

    /**
     * The original social media post's caption or body text.
     */
    @Schema(
        description = "The original post's caption text, separate from the " +
                      "claim itself. The claim is what's investigated; the " +
                      "caption is the social framing around it.",
        example = "🔥 BREAKING: Watch this leaked footage of the signing ceremony. " +
                  "They can't hide the truth anymore! #exposed #leaked"
    )
    private final String caption;

    /**
     * URL for the post's thumbnail or preview image.
     */
    @Schema(
        description = "Thumbnail URL for the preview image.",
        example = "https://assets.quaero.app/cases/1/thumb.jpg"
    )
    private final String thumbnailUrl;

    /**
     * URL for the full-size media (image or video) shown on the
     * Observe screen.
     */
    @Schema(
        description = "Full-size media URL (image or video). Only shown " +
                      "on the Observe screen, not on the Feed card.",
        example = "https://assets.quaero.app/cases/1/media.mp4"
    )
    private final String mediaUrl;

    /**
     * The type of media attached to the original post.
     */
    @Schema(
        description = "Type of media attached to the original post " +
                      "(IMAGE, VIDEO, TEXT_ONLY, SCREENSHOT).",
        example = "VIDEO"
    )
    private final MediaType mediaType;

    /**
     * Display-only engagement count — likes on the original post.
     */
    @Schema(description = "Number of likes on the original post.", example = "24500")
    private final Integer engagementLikes;

    /**
     * Display-only engagement count — comments on the original post.
     */
    @Schema(description = "Number of comments on the original post.", example = "1832")
    private final Integer engagementComments;

    /**
     * Display-only engagement count — shares on the original post.
     */
    @Schema(description = "Number of shares/retweets on the original post.", example = "8700")
    private final Integer engagementShares;

    /**
     * The original post's claimed publish date.
     */
    @Schema(
        description = "When the original social media post was published. " +
                      "Distinct from the case's creation date in QUAERO.",
        example = "2024-03-14T08:30:00"
    )
    private final LocalDateTime publishedAt;

    /**
     * Investigation difficulty level for this challenge.
     */
    @Schema(
        description = "Verification difficulty level (EASY, MEDIUM, HARD). " +
                      "Defaults to MEDIUM.",
        example = "MEDIUM"
    )
    private final VerificationDifficulty verificationDifficulty;

    /**
     * Topical category of this Investigation Challenge.
     */
    @Schema(
        description = "Topical category (POLITICS, SCIENCE, HEALTH, etc.). " +
                      "Null for legacy cases.",
        example = "POLITICS"
    )
    private final Category category;
}

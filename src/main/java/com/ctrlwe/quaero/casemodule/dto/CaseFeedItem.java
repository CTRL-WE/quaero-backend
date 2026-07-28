package com.ctrlwe.quaero.casemodule.dto;

import com.ctrlwe.quaero.casemodule.entity.Category;
import com.ctrlwe.quaero.casemodule.entity.MediaType;
import com.ctrlwe.quaero.casemodule.entity.Platform;
import com.ctrlwe.quaero.casemodule.entity.VerificationDifficulty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Public DTO representing a single Investigation Challenge on the
 * Investigation Feed.
 *
 * <p>This class carries only the fields needed for a scrollable
 * feed card:</p>
 * <ul>
 *   <li>Investigative content: {@link #id}, {@link #claim},
 *       {@link #evidenceTeaser}, {@link #alreadyCompleted}</li>
 *   <li>Presentation metadata (V2 "Original Post"): {@link #platform},
 *       {@link #thumbnailUrl}, {@link #mediaType},
 *       {@link #verificationDifficulty}, engagement counts,
 *       {@link #category}</li>
 * </ul>
 *
 * <p>Intentionally <strong>omitted</strong> from the Feed card
 * (available only on the Observe/Brief screen):</p>
 * <ul>
 *   <li>{@code originalPoster} — full poster info is Observe-only</li>
 *   <li>{@code caption} — full post caption is Observe-only</li>
 *   <li>{@code mediaUrl} — full-size media is Observe-only;
 *       the Feed uses {@link #thumbnailUrl} for a lightweight preview</li>
 *   <li>{@code publishedAt} — post date detail is Observe-only</li>
 * </ul>
 *
 * <p><strong>No field from the internal shape</strong>
 * ({@code groundTruth}, {@code groundTruthExplanation},
 * {@code trustedReferences}, {@code investigationHints},
 * {@code learningSummary}) may ever be added to this class.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@Schema(
    name = "CaseFeedItem",
    description = "A single Investigation Challenge on the Investigation Feed. " +
                  "Contains the claim, a lightweight evidence teaser, platform " +
                  "metadata, engagement metrics, and a difficulty badge — " +
                  "enough for a scrollable card. No internal case content."
)
public class CaseFeedItem {

    // ----------------------------------------------------------------
    // Investigative content
    // ----------------------------------------------------------------

    /**
     * Unique case identifier. Used by the client to navigate to the
     * Observe screen.
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
     * A truncated teaser of the public evidence summary (≤ 150 characters,
     * with {@code "..."} appended if the original was longer).
     * The full summary is available on the Observe endpoint.
     */
    @Schema(
        description = "Truncated preview of the public evidence summary " +
                      "(max 150 characters, ellipsis-terminated if truncated). " +
                      "Fetch GET /api/cases/{id}/brief for the full text.",
        example = "Multiple news outlets reported the video on the same day. " +
                  "Reverse image search tools are publicly available...",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final String evidenceTeaser;

    /**
     * Whether the requesting user has already submitted their reasoning
     * for this Investigation Challenge.
     *
     * <p>Completed cases are still shown on the Feed (they are never hidden),
     * but this flag allows the frontend to render a visual indicator
     * (e.g. a checkmark) so the user knows they have already investigated it.</p>
     *
     * <!-- TODO: Wire to Submission module.
     *      The Submission module now exists. Replace this stub with a call to
     *      SubmissionService (through its service interface) checking whether
     *      the user has an existing submission for this case.
     *      The method signature getFeed(Long userId) already receives userId
     *      so no CaseController changes are needed at that point. -->
     */
    @Schema(
        description = "True if the authenticated user has already submitted a " +
                      "reasoning verdict for this Investigation Challenge. " +
                      "Always false until the Submission module is wired in.",
        example = "false",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final boolean alreadyCompleted;

    // ----------------------------------------------------------------
    // Presentation metadata — "Original Post" (V2 architecture)
    // ----------------------------------------------------------------

    /**
     * The social media platform the original post came from.
     * Drives platform-specific styling on the feed card (logo, colours).
     */
    @Schema(
        description = "Social media platform of the original post. " +
                      "Null for legacy cases created before V2.",
        example = "INSTAGRAM"
    )
    private final Platform platform;

    /**
     * URL for the post's thumbnail or preview image on the feed card.
     */
    @Schema(
        description = "Thumbnail URL for the feed card preview image. " +
                      "Null if no media is attached or for legacy cases.",
        example = "https://assets.quaero.app/cases/1/thumb.jpg"
    )
    private final String thumbnailUrl;

    /**
     * The type of media attached to the original post.
     */
    @Schema(
        description = "Type of media attached to the original post " +
                      "(IMAGE, VIDEO, TEXT_ONLY, SCREENSHOT). " +
                      "Null for legacy cases.",
        example = "IMAGE"
    )
    private final MediaType mediaType;

    /**
     * Investigation difficulty badge displayed on the feed card.
     */
    @Schema(
        description = "Verification difficulty level for this Investigation " +
                      "Challenge (EASY, MEDIUM, HARD). Defaults to MEDIUM.",
        example = "MEDIUM",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private final VerificationDifficulty verificationDifficulty;

    /**
     * Display-only engagement count — likes on the original post.
     */
    @Schema(
        description = "Number of likes on the original post. " +
                      "Static, display-only. Defaults to 0.",
        example = "24500"
    )
    private final Integer engagementLikes;

    /**
     * Display-only engagement count — comments on the original post.
     */
    @Schema(
        description = "Number of comments on the original post. " +
                      "Static, display-only. Defaults to 0.",
        example = "1832"
    )
    private final Integer engagementComments;

    /**
     * Display-only engagement count — shares on the original post.
     */
    @Schema(
        description = "Number of shares/retweets on the original post. " +
                      "Static, display-only. Defaults to 0.",
        example = "8700"
    )
    private final Integer engagementShares;

    /**
     * Topical category of this Investigation Challenge.
     */
    @Schema(
        description = "Topical category (POLITICS, SCIENCE, HEALTH, etc.). " +
                      "Null for legacy cases or uncategorised challenges.",
        example = "POLITICS"
    )
    private final Category category;
}

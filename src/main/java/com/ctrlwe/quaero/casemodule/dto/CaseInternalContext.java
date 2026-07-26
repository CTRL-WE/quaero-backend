package com.ctrlwe.quaero.casemodule.dto;

import com.ctrlwe.quaero.casemodule.entity.CaseStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Internal-only projection of a {@link com.ctrlwe.quaero.casemodule.entity.Case}.
 *
 * <p><strong>THIS CLASS MUST NEVER BE RETURNED FROM ANY {@code @RestController}
 * METHOD.</strong> It is consumed exclusively by the future Investigation and
 * Submission &amp; Evaluation service modules through
 * {@code CaseService#getFullContext(Long)} — not through any HTTP endpoint.</p>
 *
 * <p>It deliberately carries <em>all</em> fields, including ground truth and
 * every other internal field, so that downstream modules have everything they
 * need in one call. This is intentional: the isolation guarantee is enforced
 * by keeping this class off every controller return type and off the OpenAPI
 * schema, not by omitting fields from the internal context itself.</p>
 *
 * <p>If you are reading this while working on a controller, close this file.
 * Public responses use {@link CaseFeedItem} and {@link CaseBriefResponse}
 * only.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
public class CaseInternalContext {

    // ----------------------------------------------------------------
    // Public-shape fields (also present in CaseBriefResponse / CaseFeedItem)
    // ----------------------------------------------------------------

    /** Surrogate database identifier. */
    private final Long id;

    /** The real-world claim being investigated. */
    private final String claim;

    /** Full public evidence summary (shown on Brief screen). */
    private final String publicEvidenceSummary;

    // ----------------------------------------------------------------
    // Internal-shape fields — NEVER in any HTTP response
    // ----------------------------------------------------------------

    /**
     * The verified factual conclusion about the claim.
     * Used by AI grading — never exposed pre-submission.
     */
    private final String groundTruth;

    /**
     * Explanation of why {@link #groundTruth} is correct.
     * Revealed post-submission by the Submission &amp; Evaluation module.
     */
    private final String groundTruthExplanation;

    /**
     * Newline-delimited trusted references (one per line).
     * Used by the Submission &amp; Evaluation module when assembling
     * post-submission feedback.
     */
    private final String trustedReferences;

    /**
     * Internal hints for the AI Mentor's Socratic questioning.
     * Never exposed to users, at any point in the investigation lifecycle.
     */
    private final String investigationHints;

    /**
     * Structured learning summary revealed after submission.
     * Never exposed pre-submission under any circumstance.
     */
    private final String learningSummary;

    // ----------------------------------------------------------------
    // Lifecycle metadata
    // ----------------------------------------------------------------

    /** Lifecycle status of the case at the time of retrieval. */
    private final CaseStatus status;

    /** Timestamp at which this case was created. */
    private final LocalDateTime createdAt;
}

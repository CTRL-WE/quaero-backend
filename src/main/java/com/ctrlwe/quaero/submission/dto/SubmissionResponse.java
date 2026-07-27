package com.ctrlwe.quaero.submission.dto;

import com.ctrlwe.quaero.submission.entity.ConfidenceLevel;
import com.ctrlwe.quaero.submission.entity.EvidenceType;
import com.ctrlwe.quaero.submission.entity.SubmissionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Full response payload representing a single submission.
 *
 * <p>Contains all user-visible fields of the
 * {@link com.ctrlwe.quaero.submission.entity.Submission} entity,
 * including relationship IDs ({@code caseId}, {@code userId}),
 * enum values, and audit timestamps.</p>
 *
 * <p>This DTO is used whenever a complete representation of a
 * submission is required (e.g. detail views, creation responses).</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {

    /**
     * The unique identifier of the submission.
     */
    private Long id;

    /**
     * The identifier of the case this submission belongs to.
     */
    private Long caseId;

    /**
     * The identifier of the user who created this submission.
     */
    private Long userId;

    /**
     * A concise title summarising the submission's claim or argument.
     */
    private String title;

    /**
     * The full body of the submission, containing the user's reasoning
     * and analysis.
     */
    private String description;

    /**
     * The name of the primary source referenced in the submission.
     */
    private String sourceName;

    /**
     * The URL of the primary source referenced in the submission.
     */
    private String sourceUrl;

    /**
     * The category of evidence provided in this submission.
     */
    private EvidenceType evidenceType;

    /**
     * The submitter's self-assessed confidence in the accuracy of
     * their evidence and reasoning.
     */
    private ConfidenceLevel confidenceLevel;

    /**
     * The current lifecycle status of this submission.
     */
    private SubmissionStatus status;

    /**
     * Timestamp at which this submission was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp of the most recent modification to this submission.
     */
    private LocalDateTime updatedAt;
}

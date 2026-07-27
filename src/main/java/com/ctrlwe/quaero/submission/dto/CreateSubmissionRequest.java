package com.ctrlwe.quaero.submission.dto;

import com.ctrlwe.quaero.submission.entity.ConfidenceLevel;
import com.ctrlwe.quaero.submission.entity.EvidenceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating a new submission.
 *
 * <p>Carries the user-provided content for a submission. Fields that
 * are system-managed ({@code id}, {@code status}, {@code caseId},
 * {@code userId}, {@code createdAt}, {@code updatedAt}) are
 * intentionally excluded — they are set by the service layer.</p>
 *
 * <p>All mandatory fields are validated automatically by the Bean
 * Validation framework when the controller method is annotated with
 * {@code @Valid}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubmissionRequest {

    /**
     * A concise title summarising the submission's claim or argument.
     */
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    /**
     * The full body of the submission, containing the user's reasoning
     * and analysis.
     */
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    /**
     * The name of the primary source referenced in the submission
     * (e.g. publication name, author, or organisation).
     */
    @Size(max = 255, message = "Source name must not exceed 255 characters")
    private String sourceName;

    /**
     * The URL of the primary source referenced in the submission.
     */
    @Size(max = 2048, message = "Source URL must not exceed 2048 characters")
    private String sourceUrl;

    /**
     * The category of evidence provided in this submission.
     */
    @NotNull(message = "Evidence type is required")
    private EvidenceType evidenceType;

    /**
     * The submitter's self-assessed confidence in the accuracy of
     * their evidence and reasoning.
     */
    @NotNull(message = "Confidence level is required")
    private ConfidenceLevel confidenceLevel;
}

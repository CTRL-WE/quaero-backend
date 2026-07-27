package com.ctrlwe.quaero.submission.dto;

import com.ctrlwe.quaero.submission.entity.ConfidenceLevel;
import com.ctrlwe.quaero.submission.entity.EvidenceType;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating an existing submission.
 *
 * <p>All fields are optional to support partial updates — only
 * non-{@code null} values should be applied to the existing entity.
 * The service layer is responsible for merging supplied fields with
 * the current state of the submission.</p>
 *
 * <p>System-managed fields ({@code id}, {@code status}, {@code caseId},
 * {@code userId}, {@code createdAt}, {@code updatedAt}) are
 * intentionally excluded.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubmissionRequest {

    /**
     * Updated title for the submission. If {@code null}, the existing
     * title is retained.
     */
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    /**
     * Updated description body. If {@code null}, the existing
     * description is retained.
     */
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    /**
     * Updated source name. If {@code null}, the existing source name
     * is retained.
     */
    @Size(max = 255, message = "Source name must not exceed 255 characters")
    private String sourceName;

    /**
     * Updated source URL. If {@code null}, the existing source URL
     * is retained.
     */
    @Size(max = 2048, message = "Source URL must not exceed 2048 characters")
    private String sourceUrl;

    /**
     * Updated evidence type. If {@code null}, the existing evidence
     * type is retained.
     */
    private EvidenceType evidenceType;

    /**
     * Updated confidence level. If {@code null}, the existing
     * confidence level is retained.
     */
    private ConfidenceLevel confidenceLevel;
}

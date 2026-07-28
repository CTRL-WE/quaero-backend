package com.ctrlwe.quaero.submission.mapper;

import com.ctrlwe.quaero.submission.dto.CreateSubmissionRequest;
import com.ctrlwe.quaero.submission.dto.SubmissionResponse;
import com.ctrlwe.quaero.submission.dto.SubmissionSummaryResponse;
import com.ctrlwe.quaero.submission.dto.UpdateSubmissionRequest;
import com.ctrlwe.quaero.submission.entity.Submission;
import com.ctrlwe.quaero.submission.entity.SubmissionStatus;
import org.springframework.stereotype.Component;

/**
 * Maps between {@link Submission} entities and submission-related DTOs.
 *
 * <p>All mapping is performed manually, field-by-field. No generic
 * object mapper (ModelMapper, MapStruct, BeanUtils) is used — this
 * ensures full control over which fields are exposed in each DTO
 * and prevents accidental data leakage.</p>
 *
 * <p>If you add a field to a response DTO, you <strong>must</strong>
 * add a corresponding mapping line in this class. There is no
 * automatic copy path that could silently include a field.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Component
public class SubmissionMapper {

    /**
     * Maps a {@link CreateSubmissionRequest} to a new {@link Submission}
     * entity, associating it with the given user and case by their IDs.
     *
     * <p>The returned entity has its {@link SubmissionStatus} set to
     * {@link SubmissionStatus#PENDING}. Audit timestamps
     * ({@code createdAt}, {@code updatedAt}) will be set automatically
     * by JPA auditing on persist.</p>
     *
     * @param request the creation request DTO
     * @param userId  the ID of the user creating the submission
     * @param caseId  the ID of the case this submission belongs to
     * @return a new, unpersisted {@link Submission} entity
     */
    public Submission toEntity(CreateSubmissionRequest request,
                               Long userId,
                               Long caseId) {
        return Submission.builder()
                .userId(userId)
                .caseId(caseId)
                .title(request.getTitle())
                .description(request.getDescription())
                .sourceName(request.getSourceName())
                .sourceUrl(request.getSourceUrl())
                .evidenceType(request.getEvidenceType())
                .confidenceLevel(request.getConfidenceLevel())
                .status(SubmissionStatus.PENDING)
                .build();
    }

    /**
     * Maps a {@link Submission} entity to a full
     * {@link SubmissionResponse} DTO.
     *
     * <p>Includes all user-visible fields, relationship IDs
     * ({@code caseId}, {@code userId}), enum values, and audit
     * timestamps.</p>
     *
     * @param submission the submission entity to map
     * @return a fully populated {@link SubmissionResponse}
     */
    public SubmissionResponse toResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .caseId(submission.getCaseId())
                .userId(submission.getUserId())
                .title(submission.getTitle())
                .description(submission.getDescription())
                .sourceName(submission.getSourceName())
                .sourceUrl(submission.getSourceUrl())
                .evidenceType(submission.getEvidenceType())
                .confidenceLevel(submission.getConfidenceLevel())
                .status(submission.getStatus())
                .createdAt(submission.getCreatedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }

    /**
     * Maps a {@link Submission} entity to a lightweight
     * {@link SubmissionSummaryResponse} DTO.
     *
     * <p>Contains only the minimal fields needed for list views
     * and feed cards.</p>
     *
     * @param submission the submission entity to map
     * @return a populated {@link SubmissionSummaryResponse}
     */
    public SubmissionSummaryResponse toSummary(Submission submission) {
        return SubmissionSummaryResponse.builder()
                .id(submission.getId())
                .title(submission.getTitle())
                .sourceName(submission.getSourceName())
                .status(submission.getStatus())
                .build();
    }

    /**
     * Applies the non-{@code null} fields from an
     * {@link UpdateSubmissionRequest} to an existing
     * {@link Submission} entity.
     *
     * <p>Only mutable content fields are updated; system-managed
     * fields ({@code id}, {@code status}, {@code userId},
     * {@code caseId}, {@code createdAt}) are never modified.
     * The {@code updatedAt} timestamp is handled by JPA auditing.</p>
     *
     * @param submission the existing entity to update in place
     * @param request    the update request containing the new values
     */
    public void updateEntity(Submission submission,
                             UpdateSubmissionRequest request) {
        if (request.getTitle() != null) {
            submission.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            submission.setDescription(request.getDescription());
        }
        if (request.getSourceName() != null) {
            submission.setSourceName(request.getSourceName());
        }
        if (request.getSourceUrl() != null) {
            submission.setSourceUrl(request.getSourceUrl());
        }
        if (request.getEvidenceType() != null) {
            submission.setEvidenceType(request.getEvidenceType());
        }
        if (request.getConfidenceLevel() != null) {
            submission.setConfidenceLevel(request.getConfidenceLevel());
        }
    }
}

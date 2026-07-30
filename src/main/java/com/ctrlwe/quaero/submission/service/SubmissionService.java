package com.ctrlwe.quaero.submission.service;

import com.ctrlwe.quaero.submission.dto.CreateSubmissionRequest;
import com.ctrlwe.quaero.submission.dto.SubmissionResponse;
import com.ctrlwe.quaero.submission.dto.SubmissionSummaryResponse;
import com.ctrlwe.quaero.submission.dto.UpdateSubmissionRequest;

import java.util.List;

/**
 * Service interface for the Submission module.
 *
 * <p>This interface is the <strong>only</strong> permitted entry point
 * into Submission module logic from outside the module. The
 * cross-module access rule for this codebase states that no module
 * may reach into another module's repository or entity directly —
 * all inter-module calls must go through the owning module's service
 * interface.</p>
 *
 * <p>The methods on this interface cover the full CRUD lifecycle of
 * a submission:</p>
 * <ol>
 *   <li>{@link #createSubmission} — create a new evidence submission.</li>
 *   <li>{@link #updateSubmission} — update mutable fields (owner only).</li>
 *   <li>{@link #getSubmission} — retrieve a single submission by ID.</li>
 *   <li>{@link #getSubmissionsForCase} — list all submissions for a case.</li>
 *   <li>{@link #deleteSubmission} — delete a submission (owner only).</li>
 * </ol>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface SubmissionService {

    /**
     * Creates a new evidence submission for the specified case.
     *
     * <p>The submission is automatically assigned
     * {@link com.ctrlwe.quaero.submission.entity.SubmissionStatus#PENDING}
     * status at creation time.</p>
     *
     * @param userId  the ID of the authenticated user creating the submission
     * @param caseId  the ID of the case this submission belongs to
     * @param request the creation request containing title, description,
     *                source info, evidence type, and confidence level
     * @return the created submission's full response
     * @throws com.ctrlwe.quaero.exception.ResourceNotFoundException
     *         if the user or case does not exist
     * @throws com.ctrlwe.quaero.exception.BadRequestException
     *         if the request fails business validation
     */
    SubmissionResponse createSubmission(Long userId,
                                        Long caseId,
                                        CreateSubmissionRequest request);

    /**
     * Updates an existing submission's mutable fields.
     *
     * <p>Only the submission owner may perform this operation.
     * System-managed fields (status, timestamps, ownership) are
     * never modified by this method.</p>
     *
     * @param submissionId the ID of the submission to update
     * @param userId       the ID of the authenticated user requesting
     *                     the update (must be the owner)
     * @param request      the update request containing the fields to modify
     * @return the updated submission's full response
     * @throws com.ctrlwe.quaero.submission.exception.SubmissionNotFoundException
     *         if no submission with the given ID exists
     * @throws com.ctrlwe.quaero.exception.UnauthorizedException
     *         if the requesting user is not the submission owner
     * @throws com.ctrlwe.quaero.exception.BadRequestException
     *         if the request fails business validation
     */
    SubmissionResponse updateSubmission(Long submissionId,
                                        Long userId,
                                        UpdateSubmissionRequest request);

    /**
     * Retrieves a single submission by its ID.
     *
     * @param submissionId the ID of the submission to retrieve
     * @return the submission's full response
     * @throws com.ctrlwe.quaero.submission.exception.SubmissionNotFoundException
     *         if no submission with the given ID exists
     */
    SubmissionResponse getSubmission(Long submissionId);

    /**
     * Retrieves all submissions for the specified case, ordered by
     * creation date descending (newest first).
     *
     * @param caseId the ID of the case to retrieve submissions for
     * @return an ordered list of submission summaries; may be empty
     */
    List<SubmissionSummaryResponse> getSubmissionsForCase(Long caseId);

    /**
     * Deletes a submission by its ID.
     *
     * <p>Only the submission owner may perform this operation.</p>
     *
     * @param submissionId the ID of the submission to delete
     * @param userId       the ID of the authenticated user requesting
     *                     the deletion (must be the owner)
     * @throws com.ctrlwe.quaero.submission.exception.SubmissionNotFoundException
     *         if no submission with the given ID exists
     * @throws com.ctrlwe.quaero.exception.UnauthorizedException
     *         if the requesting user is not the submission owner
     */
    void deleteSubmission(Long submissionId, Long userId);

    /**
     * Returns {@code true} if the given user has at least one submission
     * for the given case, regardless of the submission's lifecycle status.
     *
     * <p>This is a lightweight existence check — no submission entity is
     * loaded. It is called by the Case module's feed endpoint to populate
     * the {@code alreadyCompleted} flag on each
     * {@link com.ctrlwe.quaero.casemodule.dto.CaseFeedItem}. Callers
     * must <strong>not</strong> reach into the Submission repository or
     * entity directly — this method is the only permitted cross-module
     * access point for this check.</p>
     *
     * @param userId the ID of the user to check
     * @param caseId the ID of the case to check
     * @return {@code true} if a submission exists for {@code (userId, caseId)}
     */
    boolean hasSubmittedForCase(Long userId, Long caseId);
}

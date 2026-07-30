package com.ctrlwe.quaero.submission.service;

import com.ctrlwe.quaero.ai.dto.GradingResult;
import com.ctrlwe.quaero.ai.service.AiClientService;
import com.ctrlwe.quaero.casemodule.dto.CaseInternalContext;
import com.ctrlwe.quaero.casemodule.exception.CaseNotFoundException;
import com.ctrlwe.quaero.casemodule.repository.CaseRepository;
import com.ctrlwe.quaero.casemodule.service.CaseService;
import com.ctrlwe.quaero.exception.ErrorCode;
import com.ctrlwe.quaero.exception.ResourceNotFoundException;
import com.ctrlwe.quaero.exception.UnauthorizedException;
import com.ctrlwe.quaero.reputation.dto.ProgressionUpdateResult;
import com.ctrlwe.quaero.reputation.service.ReputationService;
import com.ctrlwe.quaero.submission.dto.CreateSubmissionRequest;
import com.ctrlwe.quaero.submission.dto.SubmissionResponse;
import com.ctrlwe.quaero.submission.dto.SubmissionSummaryResponse;
import com.ctrlwe.quaero.submission.dto.UpdateSubmissionRequest;
import com.ctrlwe.quaero.submission.entity.Submission;
import com.ctrlwe.quaero.submission.exception.SubmissionNotFoundException;
import com.ctrlwe.quaero.submission.mapper.SubmissionMapper;
import com.ctrlwe.quaero.submission.repository.SubmissionRepository;
import com.ctrlwe.quaero.submission.validator.SubmissionValidator;
import com.ctrlwe.quaero.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link SubmissionService}.
 *
 * <p>All DTO construction is delegated to {@link SubmissionMapper}
 * which performs explicit, field-by-field mapping. No generic object
 * mapper is used.</p>
 *
 * <p>Business validation is delegated to {@link SubmissionValidator}
 * which enforces domain rules (blank checks, URL format, required
 * enums) before any persistence operation.</p>
 *
 * <p>Ownership enforcement: only the user who created a submission
 * may update or delete it. Attempts by other users result in an
 * {@link UnauthorizedException}.</p>
 *
 * <h3>Cross-module boundary rule</h3>
 * <p>The {@link Submission} entity stores foreign keys as plain
 * {@code Long} columns ({@code userId}, {@code caseId}) rather than
 * {@code @ManyToOne} associations. Existence checks for users and
 * cases are performed via {@code existsById()} on the respective
 * repositories — no cross-module entity is loaded.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final CaseRepository caseRepository;
    private final CaseService caseService;
    private final AiClientService aiClientService;
    private final SubmissionMapper submissionMapper;
    private final SubmissionValidator submissionValidator;
    private final ReputationService reputationService;

    /**
     * {@inheritDoc}
     *
     * <p>Verifies that the user and case exist, validates the request
     * via {@link SubmissionValidator#validateCreate}, maps the request
     * to a new entity with status {@code PENDING}, persists it,
     * and returns the full response.</p>
     */
    @Override
    @Transactional
    public SubmissionResponse createSubmission(Long userId,
                                               Long caseId,
                                               CreateSubmissionRequest request) {
        log.debug("createSubmission called for userId={}, caseId={}", userId, caseId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "User with id " + userId + " does not exist",
                    ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (!caseRepository.existsById(caseId)) {
            throw new CaseNotFoundException(caseId);
        }

        submissionValidator.validateCreate(request);

        Submission submission = submissionMapper.toEntity(request, userId, caseId);
        Submission savedSubmission = submissionRepository.save(submission);

        log.info("Submission created successfully: id={}, caseId={}, userId={}",
                savedSubmission.getId(), caseId, userId);

        // ── AI grading ────────────────────────────────────────────────────────────────────
        // Fetch the full case context (ground truth + claim) required by the grading prompt.
        // CaseService.getFullContext() never returns null and propagates CaseNotFoundException
        // if the case has been deleted between the existsById check above and this call
        // (an unlikely but safe failure path — the transaction rolls back).
        CaseInternalContext caseContext = caseService.getFullContext(caseId);

        // Build the evidence list. sourceUrl may be null/blank; include it only when present.
        List<String> evidenceLinks = (request.getSourceUrl() != null && !request.getSourceUrl().isBlank())
                ? Collections.singletonList(request.getSourceUrl())
                : Collections.emptyList();

        // Grade the submission. Never throws — returns GradingResult.ofDegraded() on any
        // AI failure, giving score=0 and degraded=true. This matches the pattern used by
        // InvestigationServiceImpl.callAi() for Socratic responses.
        GradingResult gradingResult = aiClientService.getGradingResult(
                request.getDescription(),
                evidenceLinks,
                caseContext.getClaim() + "\n" + caseContext.getGroundTruth());

        if (gradingResult.degraded()) {
            log.warn("Degraded AI grading result for submissionId={} (userId={}, caseId={}) "
                    + "— score defaults to 0",
                    savedSubmission.getId(), userId, caseId);
        }

        // ── Reputation integration: joins this @Transactional context ─────────────────────
        int reasoningScore = gradingResult.score();
        ProgressionUpdateResult progression =
                reputationService.recordSubmissionOutcome(userId, reasoningScore);

        return submissionMapper.toCreationResponse(savedSubmission, progression);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves the existing submission, verifies ownership,
     * validates the update request, applies only the non-null
     * mutable fields, saves, and returns the updated response.</p>
     */
    @Override
    @Transactional
    public SubmissionResponse updateSubmission(Long submissionId,
                                               Long userId,
                                               UpdateSubmissionRequest request) {
        log.debug("updateSubmission called for submissionId={}, userId={}",
                submissionId, userId);

        Submission submission = findSubmissionOrThrow(submissionId);
        verifyOwnership(submission, userId);

        submissionValidator.validateUpdate(request);
        submissionMapper.updateEntity(submission, request);

        Submission updatedSubmission = submissionRepository.save(submission);

        log.info("Submission updated successfully: id={}", submissionId);

        return submissionMapper.toResponse(updatedSubmission);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Looks up the submission by ID and throws
     * {@link SubmissionNotFoundException} if not found.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(Long submissionId) {
        log.debug("getSubmission called for submissionId={}", submissionId);

        Submission submission = findSubmissionOrThrow(submissionId);
        return submissionMapper.toResponse(submission);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to the repository's
     * {@code findAllByCaseIdOrderByCreatedAtDesc} query and maps
     * each entity to a lightweight summary DTO.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<SubmissionSummaryResponse> getSubmissionsForCase(Long caseId) {
        log.debug("getSubmissionsForCase called for caseId={}", caseId);

        return submissionRepository
                .findAllByCaseIdOrderByCreatedAtDesc(caseId)
                .stream()
                .map(submissionMapper::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retrieves the submission, verifies ownership, and deletes
     * the entity.</p>
     */
    @Override
    @Transactional
    public void deleteSubmission(Long submissionId, Long userId) {
        log.debug("deleteSubmission called for submissionId={}, userId={}",
                submissionId, userId);

        Submission submission = findSubmissionOrThrow(submissionId);
        verifyOwnership(submission, userId);

        submissionRepository.delete(submission);

        log.info("Submission deleted successfully: id={}", submissionId);
    }

    // ──────────────────────────────────────────────
    //  Private helpers
    // ──────────────────────────────────────────────

    /**
     * Retrieves a submission by ID or throws
     * {@link SubmissionNotFoundException}.
     *
     * @param submissionId the submission ID to look up
     * @return the submission entity
     * @throws SubmissionNotFoundException if not found
     */
    private Submission findSubmissionOrThrow(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new SubmissionNotFoundException(
                        "Submission with id " + submissionId + " does not exist"));
    }

    /**
     * Verifies that the given user is the owner of the submission.
     *
     * @param submission the submission to check
     * @param userId     the ID of the user making the request
     * @throws UnauthorizedException if the user is not the owner
     */
    private void verifyOwnership(Submission submission, Long userId) {
        if (!submission.getUserId().equals(userId)) {
            throw new UnauthorizedException(
                    "User with id " + userId
                            + " is not the owner of submission with id "
                            + submission.getId(),
                    ErrorCode.UNAUTHORIZED);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to a lightweight repository {@code SELECT EXISTS} query.
     * No entity is loaded. This method intentionally carries no
     * {@code @Transactional} annotation — it can safely run without a
     * transaction or join an existing one via Spring's default
     * {@code REQUIRED} propagation.</p>
     */
    @Override
    public boolean hasSubmittedForCase(Long userId, Long caseId) {
        log.debug("hasSubmittedForCase called for userId={}, caseId={}", userId, caseId);
        return submissionRepository.existsByUserIdAndCaseId(userId, caseId);
    }
}

package com.ctrlwe.quaero.casemodule.service;

import com.ctrlwe.quaero.casemodule.dto.CaseBriefResponse;
import com.ctrlwe.quaero.casemodule.dto.CaseFeedItem;
import com.ctrlwe.quaero.casemodule.dto.CaseInternalContext;
import com.ctrlwe.quaero.casemodule.entity.Case;
import com.ctrlwe.quaero.casemodule.entity.CaseStatus;
import com.ctrlwe.quaero.casemodule.exception.CaseNotFoundException;
import com.ctrlwe.quaero.casemodule.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of {@link CaseService}.
 *
 * <p>All DTO construction is performed by explicit, field-by-field mapping
 * inside this class. No generic object mapper (ModelMapper, MapStruct,
 * BeanUtils) is used — this is the primary enforcement mechanism for the
 * public/internal split. If you add a field to a public DTO, you must
 * consciously add a mapping line here; there is no automatic copy path
 * that could silently include an internal field.</p>
 *
 * <p><strong>Teaser truncation rule:</strong> {@link #getFeed(Long)} truncates
 * {@code publicEvidenceSummary} to 150 characters and appends {@code "..."}
 * if the original text is longer. This produces a consistent card-size preview
 * on the Feed. The threshold is declared as a named constant
 * ({@link #TEASER_MAX_LENGTH}) so that it can be found and changed in one
 * place if the product team revises the design.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaseServiceImpl implements CaseService {

    /**
     * Maximum number of characters retained in the evidence teaser shown
     * on the Feed ({@link CaseFeedItem#getEvidenceTeaser()}).
     * If {@code publicEvidenceSummary.length() > TEASER_MAX_LENGTH},
     * the text is cut at this boundary and {@code "..."} is appended.
     */
    private static final int TEASER_MAX_LENGTH = 150;

    private final CaseRepository caseRepository;

    // ----------------------------------------------------------------
    // Public surface — wired to HTTP endpoints in CaseController
    // ----------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Fetches all PUBLISHED cases from the repository, maps each one
     * to a {@link CaseFeedItem} via explicit field-by-field mapping, and
     * returns the resulting list.</p>
     *
     * <p>The {@code alreadyCompleted} flag is stubbed as {@code false} for
     * every case. See the TODO inside {@link #toFeedItem(Case, Long)} for the
     * Submission module wiring instructions.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CaseFeedItem> getFeed(Long userId) {
        log.debug("getFeed called for userId={}", userId);

        List<Case> publishedCases =
                caseRepository.findAllByStatusOrderByCreatedAtDesc(CaseStatus.PUBLISHED);

        return publishedCases.stream()
                .map(c -> toFeedItem(c, userId))
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Looks up the case by ID and throws {@link CaseNotFoundException}
     * if it does not exist. Maps to {@link CaseBriefResponse} via explicit
     * field-by-field mapping — only {@code id}, {@code claim}, and
     * {@code publicEvidenceSummary} are copied.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public CaseBriefResponse getBrief(Long caseId) {
        log.debug("getBrief called for caseId={}", caseId);

        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseNotFoundException(caseId));

        return toBriefResponse(c);
    }

    // ----------------------------------------------------------------
    // Internal surface — no HTTP endpoint, called by future modules only
    // ----------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Looks up the case by ID (any status) and throws
     * {@link CaseNotFoundException} if it does not exist. Returns the
     * complete {@link CaseInternalContext} including all internal fields.</p>
     *
     * <p><strong>This method must never be called from any controller.</strong>
     * It has no HTTP endpoint. It will be called by the Investigation and
     * Submission &amp; Evaluation modules through this service interface
     * once those modules are built.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public CaseInternalContext getFullContext(Long caseId) {
        log.debug("getFullContext called for caseId={}", caseId);

        Case c = caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseNotFoundException(caseId));

        return toInternalContext(c);
    }

    // ----------------------------------------------------------------
    // Private mapping methods — explicit, field-by-field only
    // ----------------------------------------------------------------

    /**
     * Maps a {@link Case} entity to a {@link CaseFeedItem}.
     *
     * <p>Only {@code id}, {@code claim}, the truncated evidence teaser, and
     * the stubbed {@code alreadyCompleted} flag are mapped. No internal field
     * is referenced in this method.</p>
     *
     * @param c      the case entity
     * @param userId the requesting user's ID (for completion-status check)
     * @return a populated {@link CaseFeedItem}
     */
    private CaseFeedItem toFeedItem(Case c, Long userId) {
        // TODO: Replace the alreadyCompleted stub with a real call to
        //       SubmissionService#hasUserSubmitted(userId, c.getId()) once the
        //       Submission module is built. Inject SubmissionService into this
        //       class through its interface — never reach into SubmissionRepository
        //       directly. The userId parameter is already threaded through from
        //       the controller, so no signature change will be needed here.
        boolean alreadyCompleted = false;

        return CaseFeedItem.builder()
                .id(c.getId())
                .claim(c.getClaim())
                .evidenceTeaser(buildTeaser(c.getPublicEvidenceSummary()))
                .alreadyCompleted(alreadyCompleted)
                .build();
    }

    /**
     * Maps a {@link Case} entity to a {@link CaseBriefResponse}.
     *
     * <p>Only {@code id}, {@code claim}, and {@code publicEvidenceSummary}
     * are mapped. This method must never reference any internal-shape field.</p>
     *
     * @param c the case entity
     * @return a populated {@link CaseBriefResponse}
     */
    private CaseBriefResponse toBriefResponse(Case c) {
        return CaseBriefResponse.builder()
                .id(c.getId())
                .claim(c.getClaim())
                .publicEvidenceSummary(c.getPublicEvidenceSummary())
                .build();
    }

    /**
     * Maps a {@link Case} entity to a {@link CaseInternalContext}.
     *
     * <p>All fields, including every internal-shape field, are copied here.
     * This method is the only place in the codebase where internal fields
     * cross the entity/DTO boundary for internal consumers.</p>
     *
     * @param c the case entity
     * @return a fully populated {@link CaseInternalContext}
     */
    private CaseInternalContext toInternalContext(Case c) {
        return CaseInternalContext.builder()
                .id(c.getId())
                .claim(c.getClaim())
                .publicEvidenceSummary(c.getPublicEvidenceSummary())
                .groundTruth(c.getGroundTruth())
                .groundTruthExplanation(c.getGroundTruthExplanation())
                .trustedReferences(c.getTrustedReferences())
                .investigationHints(c.getInvestigationHints())
                .learningSummary(c.getLearningSummary())
                .status(c.getStatus())
                .createdAt(c.getCreatedAt())
                .build();
    }

    /**
     * Truncates {@code text} to at most {@link #TEASER_MAX_LENGTH} characters.
     * Appends {@code "..."} if the text was longer than the limit.
     *
     * @param text the source text (non-null)
     * @return the teaser string
     */
    private String buildTeaser(String text) {
        if (text.length() <= TEASER_MAX_LENGTH) {
            return text;
        }
        return text.substring(0, TEASER_MAX_LENGTH) + "...";
    }
}

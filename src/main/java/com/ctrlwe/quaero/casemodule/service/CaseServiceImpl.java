package com.ctrlwe.quaero.casemodule.service;

import com.ctrlwe.quaero.casemodule.dto.CaseBriefResponse;
import com.ctrlwe.quaero.casemodule.dto.CaseFeedItem;
import com.ctrlwe.quaero.casemodule.dto.CaseInternalContext;
import com.ctrlwe.quaero.casemodule.entity.Case;
import com.ctrlwe.quaero.casemodule.entity.CaseStatus;
import com.ctrlwe.quaero.casemodule.exception.CaseNotFoundException;
import com.ctrlwe.quaero.casemodule.repository.CaseRepository;
import com.ctrlwe.quaero.submission.service.SubmissionService;
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
 * on the Investigation Feed. The threshold is declared as a named constant
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
     * on the Investigation Feed ({@link CaseFeedItem#getEvidenceTeaser()}).
     * If {@code publicEvidenceSummary.length() > TEASER_MAX_LENGTH},
     * the text is cut at this boundary and {@code "..."} is appended.
     */
    private static final int TEASER_MAX_LENGTH = 150;

    private final CaseRepository caseRepository;

    /**
     * Cross-module dependency — accessed through its service interface only.
     * Used exclusively to power the {@code alreadyCompleted} flag on
     * {@link CaseFeedItem} via {@link SubmissionService#hasSubmittedForCase}.
     * No Submission entity or repository is referenced directly.
     */
    private final SubmissionService submissionService;

    // ----------------------------------------------------------------
    // Public surface — wired to HTTP endpoints in CaseController
    // ----------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Fetches all PUBLISHED cases from the repository, maps each one
     * to a {@link CaseFeedItem} via explicit field-by-field mapping, and
     * returns the resulting list. The Feed represents the Investigation
     * Feed in the product — a scrollable list of Investigation Challenges.</p>
     *
     * <p>The {@code alreadyCompleted} flag is populated for each case by
     * delegating to {@link com.ctrlwe.quaero.submission.service.SubmissionService#hasSubmittedForCase}
     * — any existing submission (regardless of status) marks a case as completed.</p>
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
     * field-by-field mapping — the Observe screen receives the full public
     * shape plus all presentation metadata. No internal field is ever
     * included.</p>
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
    // Internal surface — no HTTP endpoint, called by other modules only
    // ----------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * <p>Looks up the case by ID (any status) and throws
     * {@link CaseNotFoundException} if it does not exist. Returns the
     * complete {@link CaseInternalContext} including all internal fields.</p>
     *
     * <p><strong>This method must never be called from any controller.</strong>
     * It has no HTTP endpoint. It is called by the Investigation and
     * Submission modules through this service interface.</p>
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
     * <p>Maps: investigative content ({@code id}, {@code claim},
     * truncated evidence teaser, {@code alreadyCompleted}) plus
     * lightweight presentation metadata ({@code platform},
     * {@code thumbnailUrl}, {@code mediaType},
     * {@code verificationDifficulty}, engagement counts,
     * {@code category}).</p>
     *
     * <p>Intentionally omits: {@code originalPoster}, {@code caption},
     * {@code mediaUrl}, {@code publishedAt} — these are Observe-only
     * fields per V1 Section 3.</p>
     *
     * @param c      the case entity
     * @param userId the requesting user's ID (for completion-status check)
     * @return a populated {@link CaseFeedItem}
     */
    private CaseFeedItem toFeedItem(Case c, Long userId) {
        boolean alreadyCompleted = submissionService.hasSubmittedForCase(userId, c.getId());

        return CaseFeedItem.builder()
                .id(c.getId())
                .claim(c.getClaim())
                .evidenceTeaser(buildTeaser(c.getPublicEvidenceSummary()))
                .alreadyCompleted(alreadyCompleted)
                // Presentation metadata (lightweight set for Feed card)
                .platform(c.getPlatform())
                .thumbnailUrl(c.getThumbnailUrl())
                .mediaType(c.getMediaType())
                .verificationDifficulty(c.getVerificationDifficulty())
                .engagementLikes(c.getEngagementLikes())
                .engagementComments(c.getEngagementComments())
                .engagementShares(c.getEngagementShares())
                .category(c.getCategory())
                .build();
    }

    /**
     * Maps a {@link Case} entity to a {@link CaseBriefResponse}.
     *
     * <p>Maps: all public-shape fields ({@code id}, {@code claim},
     * {@code publicEvidenceSummary}) plus the full presentation
     * metadata set. This is the Observe screen — the user sees the
     * complete "Original Post" here for the first time.</p>
     *
     * <p>This method must never reference any internal-shape field.</p>
     *
     * @param c the case entity
     * @return a populated {@link CaseBriefResponse}
     */
    private CaseBriefResponse toBriefResponse(Case c) {
        return CaseBriefResponse.builder()
                .id(c.getId())
                .claim(c.getClaim())
                .publicEvidenceSummary(c.getPublicEvidenceSummary())
                // Full presentation metadata (Observe screen)
                .platform(c.getPlatform())
                .originalPoster(c.getOriginalPoster())
                .caption(c.getCaption())
                .thumbnailUrl(c.getThumbnailUrl())
                .mediaUrl(c.getMediaUrl())
                .mediaType(c.getMediaType())
                .engagementLikes(c.getEngagementLikes())
                .engagementComments(c.getEngagementComments())
                .engagementShares(c.getEngagementShares())
                .publishedAt(c.getPublishedAt())
                .verificationDifficulty(c.getVerificationDifficulty())
                .category(c.getCategory())
                .build();
    }

    /**
     * Maps a {@link Case} entity to a {@link CaseInternalContext}.
     *
     * <p>All fields, including every internal-shape field, are copied here.
     * This method is the only place in the codebase where internal fields
     * cross the entity/DTO boundary for internal consumers.</p>
     *
     * <p><strong>Presentation metadata is intentionally excluded from
     * {@link CaseInternalContext}.</strong> Engagement counts and media
     * URLs have no business in an AI grading or Socratic-mentoring
     * prompt.</p>
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

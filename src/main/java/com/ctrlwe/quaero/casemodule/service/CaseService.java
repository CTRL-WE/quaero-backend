package com.ctrlwe.quaero.casemodule.service;

import com.ctrlwe.quaero.casemodule.dto.CaseBriefResponse;
import com.ctrlwe.quaero.casemodule.dto.CaseFeedItem;
import com.ctrlwe.quaero.casemodule.dto.CaseInternalContext;
import com.ctrlwe.quaero.casemodule.exception.CaseNotFoundException;

import java.util.List;

/**
 * Service interface for the Case module.
 *
 * <p>This interface is the <strong>only</strong> permitted entry point into
 * Case module logic from outside the module. The cross-module access rule
 * for this codebase states that no module may reach into another module's
 * repository or entity directly — all inter-module calls must go through
 * the owning module's service interface. For the Case module, that interface
 * is this one.</p>
 *
 * <p>The three methods map precisely to the three use-cases this module
 * must serve:</p>
 * <ol>
 *   <li>{@link #getFeed(Long)} — drives the Feed screen (public, JWT-gated).</li>
 *   <li>{@link #getBrief(Long)} — drives the Brief screen (public, JWT-gated).</li>
 *   <li>{@link #getFullContext(Long)} — internal only; will be called by
 *       Investigation and Submission &amp; Evaluation once those modules
 *       are built. Has no HTTP endpoint, now or ever.</li>
 * </ol>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public interface CaseService {

    /**
     * Returns all published cases, each mapped to a {@link CaseFeedItem},
     * including a per-user completion status flag.
     *
     * <p>Draft cases are excluded. The evidence teaser is truncated to
     * 150 characters with {@code "..."} appended if the original is longer.
     * The {@code alreadyCompleted} flag is stubbed as {@code false} until
     * the Submission module is built and wired.</p>
     *
     * @param userId the ID of the currently authenticated user (used for
     *               the per-user completion status check)
     * @return an ordered list of feed items; empty list if no published
     *         cases exist — never {@code null}
     */
    List<CaseFeedItem> getFeed(Long userId);

    /**
     * Returns the full public Brief for a single published case.
     *
     * <p>The brief contains only {@code id}, {@code claim}, and the
     * <em>full</em> {@code publicEvidenceSummary}. No internal field
     * may ever appear in the returned object.</p>
     *
     * @param caseId the case identifier
     * @return the {@link CaseBriefResponse} for the requested case
     * @throws CaseNotFoundException if no case with the given ID exists
     *         or the case is not published
     */
    CaseBriefResponse getBrief(Long caseId);

    /**
     * Returns the complete internal context for a case, including all
     * internal fields (ground truth, explanation, references, hints,
     * learning summary).
     *
     * <p><strong>INTERNAL USE ONLY.</strong> This method must never be
     * called from any {@code @RestController}. It has no HTTP endpoint.
     * It is intended exclusively for the future Investigation and
     * Submission &amp; Evaluation modules, which will inject this
     * {@code CaseService} interface and call this method through it.</p>
     *
     * @param caseId the case identifier
     * @return the {@link CaseInternalContext} for the requested case
     * @throws CaseNotFoundException if no case with the given ID exists
     */
    CaseInternalContext getFullContext(Long caseId);
}

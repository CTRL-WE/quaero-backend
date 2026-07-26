package com.ctrlwe.quaero.casemodule.entity;

/**
 * Lifecycle status of a {@link Case}.
 *
 * <p>Only {@link #PUBLISHED} cases are ever surfaced through the
 * public API. {@link #DRAFT} cases are held internally until
 * the Product Lead confirms they are ready for investigation.</p>
 *
 * <p>Cases are <strong>immutable once published</strong>. A correction
 * to a published case must be issued as a brand-new case — never as an
 * in-place status transition back to DRAFT.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum CaseStatus {

    /**
     * The case is authored but not yet released for public investigation.
     * Draft cases must never appear in any public API response.
     */
    DRAFT,

    /**
     * The case is live and available for users to investigate.
     * This is the only status returned by the Feed and Brief endpoints.
     */
    PUBLISHED
}

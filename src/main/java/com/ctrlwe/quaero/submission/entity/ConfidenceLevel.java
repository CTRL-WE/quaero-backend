package com.ctrlwe.quaero.submission.entity;

/**
 * Enumeration of confidence levels a user can assign to a
 * {@link Submission}.
 *
 * <p>Indicates how confident the submitter is in the accuracy of their
 * evidence and reasoning. The value is persisted as a {@code VARCHAR}
 * column via {@link jakarta.persistence.EnumType#STRING}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum ConfidenceLevel {

    /** The submitter has limited confidence in their evidence. */
    LOW,

    /** The submitter has moderate confidence in their evidence. */
    MEDIUM,

    /** The submitter is highly confident in their evidence. */
    HIGH
}

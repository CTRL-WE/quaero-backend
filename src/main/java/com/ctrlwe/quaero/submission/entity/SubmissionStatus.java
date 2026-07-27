package com.ctrlwe.quaero.submission.entity;

/**
 * Enumeration of lifecycle states for a {@link Submission}.
 *
 * <p>A submission begins in the {@link #PENDING} state and transitions to
 * {@link #VERIFIED} or {@link #REJECTED} after evaluation. The value is
 * persisted as a {@code VARCHAR} column via
 * {@link jakarta.persistence.EnumType#STRING}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum SubmissionStatus {

    /** The submission has been received and is awaiting evaluation. */
    PENDING,

    /** The submission has been reviewed and accepted as valid. */
    VERIFIED,

    /** The submission has been reviewed and rejected. */
    REJECTED
}

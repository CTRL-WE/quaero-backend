package com.ctrlwe.quaero.submission.entity;

/**
 * Enumeration of evidence types that can be attached to a
 * {@link Submission}.
 *
 * <p>Each constant represents a distinct category of evidence a user
 * may provide when supporting or challenging a claim. The value is
 * persisted as a {@code VARCHAR} column via
 * {@link jakarta.persistence.EnumType#STRING}.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public enum EvidenceType {

    /** A news article or blog post. */
    ARTICLE,

    /** An academic paper, study, or formal research publication. */
    RESEARCH,

    /** A video recording (e.g. interview, documentary clip). */
    VIDEO,

    /** A photographic or graphical image used as evidence. */
    IMAGE,

    /** A formal document such as a report, legal filing, or transcript. */
    DOCUMENT,

    /** Any evidence that does not fit the other categories. */
    OTHER
}

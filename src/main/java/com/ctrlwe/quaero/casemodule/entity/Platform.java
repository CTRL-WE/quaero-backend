package com.ctrlwe.quaero.casemodule.entity;

/**
 * Social media platform from which the original post originated.
 *
 * <p>Each {@link Case} (internally: Investigation Challenge) is built
 * around a real or realistic social-media post. This enum identifies
 * which platform that post came from, enabling the frontend to render
 * platform-specific styling (logo, colour palette, layout) and the
 * backend to record provenance metadata.</p>
 *
 * <p><strong>MVP scope:</strong> this is a fixed enum per the approved
 * V1 architecture (Section 2) and V2 architecture (Section 6). The
 * long-term direction is to replace this with a dynamic Platform
 * Metadata concept — but that is deferred and documented only. For
 * now, adding a new platform means adding a new constant here.</p>
 *
 * @author Quaero Engineering
 * @since 2.0
 */
public enum Platform {

    /** Instagram post or story. */
    INSTAGRAM,

    /** Twitter / X tweet or thread. */
    TWITTER,

    /** Facebook post or share. */
    FACEBOOK,

    /** YouTube video, short, or community post. */
    YOUTUBE,

    /** Reddit post or comment. */
    REDDIT,

    /** TikTok video or story. */
    TIKTOK,

    /** Traditional news article or broadcast screenshot. */
    NEWS,

    /** WhatsApp forward or screenshot — a major misinformation vector. */
    WHATSAPP,

    /** Platform not covered by the named constants above. */
    OTHER
}

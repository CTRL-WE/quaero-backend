package com.ctrlwe.quaero.casemodule.entity;

/**
 * Type of media attached to the original social-media post that an
 * Investigation Challenge is built around.
 *
 * <p>This enum is used by the frontend to decide which media renderer
 * to use when displaying the "Original Post" on the Observe screen:
 * an image viewer, a video player, a text-only card, or a screenshot
 * frame.</p>
 *
 * @author Quaero Engineering
 * @since 2.0
 */
public enum MediaType {

    /** A static image (JPEG, PNG, WebP, etc.). */
    IMAGE,

    /** A video clip (MP4, WebM, etc.). */
    VIDEO,

    /** A text-only post with no media attachment. */
    TEXT_ONLY,

    /** A screenshot of another post, article, or document. */
    SCREENSHOT
}

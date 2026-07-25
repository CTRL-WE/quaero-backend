package com.ctrlwe.quaero.common.constants;

/**
 * Application-wide API constants.
 *
 * <p>Centralises magic strings and numeric defaults so that every module
 * references the same values. Add new constants here rather than
 * scattering literals across the codebase.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class ApiConstants {

    private ApiConstants() {
        // Utility class — prevent instantiation
    }

    // ──────────────────────────────────────────────
    //  Status Messages
    // ──────────────────────────────────────────────

    /**
     * Standard success status label.
     */
    public static final String SUCCESS = "SUCCESS";

    /**
     * Standard failure status label.
     */
    public static final String FAILED = "FAILED";

    // ──────────────────────────────────────────────
    //  Pagination Defaults
    // ──────────────────────────────────────────────

    /**
     * Default number of records per page when no size is specified.
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Upper-bound page size to prevent excessive queries.
     */
    public static final int MAX_PAGE_SIZE = 100;
}

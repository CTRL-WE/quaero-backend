-- ============================================================
-- V5__add_case_presentation_metadata.sql
-- Adds presentation metadata columns to the `cases` table.
--
-- PURPOSE
-- -------
-- The V2 architecture evolves Case from a plain-text claim into
-- an "Investigation Challenge" presented as a social-media post.
-- These new columns carry the "Original Post" metadata: which
-- platform, who posted it, what media is attached, engagement
-- metrics, and difficulty/category tags.
--
-- COMPATIBILITY
-- ------------
-- All columns are nullable or defaulted. The 3 existing seeded
-- cases (V2__seed_cases.sql) will have NULL for all new text/enum
-- columns and 0 for engagement counters — they continue to work
-- without modification.
--
-- No existing column is altered or dropped.
-- ============================================================

ALTER TABLE cases

    -- Social media platform origin
    ADD COLUMN platform                 VARCHAR(20)     NULL,

    -- Original poster display name / handle
    ADD COLUMN original_poster          VARCHAR(255)    NULL,

    -- The post's original caption text
    ADD COLUMN caption                  TEXT            NULL,

    -- Feed card thumbnail URL
    ADD COLUMN thumbnail_url            VARCHAR(2048)   NULL,

    -- Full-size media URL (Observe screen only)
    ADD COLUMN media_url                VARCHAR(2048)   NULL,

    -- Type of media attachment (IMAGE, VIDEO, TEXT_ONLY, SCREENSHOT)
    ADD COLUMN media_type               VARCHAR(20)     NULL,

    -- Engagement metrics (static, display-only)
    ADD COLUMN engagement_likes         INT             NOT NULL DEFAULT 0,
    ADD COLUMN engagement_comments      INT             NOT NULL DEFAULT 0,
    ADD COLUMN engagement_shares        INT             NOT NULL DEFAULT 0,

    -- Original post's claimed publish date
    ADD COLUMN published_at             DATETIME(6)     NULL,

    -- Investigation difficulty (defaults to MEDIUM per V1 Section 2)
    ADD COLUMN verification_difficulty  VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM',

    -- Topical category (V2 Section 5 — reserved for future filtering)
    ADD COLUMN category                 VARCHAR(20)     NULL;

-- ============================================================
-- V6__add_user_progression_fields.sql
-- Adds reputation & progression columns to the `users` table.
--
-- This migration is owned by the Reputation module.
-- All columns are additive — no existing column is altered or
-- dropped. The `updated_at` column already exists and is managed
-- by JPA auditing (@LastModifiedDate); it is NOT re-created here.
--
-- New columns:
--   total_xp                INT DEFAULT 0
--   credibility             DECIMAL(10,4) NULL
--   completed_investigations INT DEFAULT 0
--   successful_submissions   INT DEFAULT 0
--
-- Why DECIMAL(10,4)?
--   Matches the JPA @Column(precision=10, scale=4) definition on
--   User.credibility and the BigDecimal.divide(..., 4, HALF_UP) in
--   ReputationCalculator.calculateCredibility(). This gives precision
--   to four decimal places (e.g. 74.5000), keeping stored values
--   compact while retaining enough resolution for leaderboard ordering.
--
-- Ordering note:
--   The leaderboard query uses ORDER BY credibility DESC NULLS LAST,
--   total_xp DESC. An index on (credibility, total_xp) is created
--   here to support this efficiently as the user table grows.
--
-- Tables NOT touched by this migration:
--   cases, investigation_session, conversation_turn,
--   submissions, helpful_vote
-- ============================================================

ALTER TABLE users

    -- Total XP accumulated across all accepted submissions.
    -- Defaults to 0 for all existing and future users.
    ADD COLUMN total_xp INT NOT NULL DEFAULT 0,

    -- Running arithmetic mean of all accepted reasoning scores.
    -- NULL until the user's first accepted submission.
    ADD COLUMN credibility DECIMAL(10, 4) NULL,

    -- Count of investigation sessions completed by this user.
    ADD COLUMN completed_investigations INT NOT NULL DEFAULT 0,

    -- Count of submissions that were accepted (graded) successfully.
    ADD COLUMN successful_submissions INT NOT NULL DEFAULT 0;

-- Composite index to support the canonical leaderboard ordering:
-- ORDER BY credibility DESC NULLS LAST, total_xp DESC
-- Only created if it doesn't already exist (idempotent-friendly pattern).
CREATE INDEX idx_user_progression
    ON users (credibility, total_xp);

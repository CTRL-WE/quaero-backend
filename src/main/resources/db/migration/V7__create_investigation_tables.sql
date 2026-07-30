-- ============================================================
-- V7__create_investigation_tables.sql
-- Creates the `investigation_sessions` and `conversation_turns`
-- tables for the Investigation module.
--
-- This migration is owned by the Investigation module.
-- It must run after V4 (submissions) because investigation_sessions
-- references users and cases, and conversation_turns references
-- investigation_sessions.
--
-- investigation_sessions
-- ----------------------
-- One row per (user, case) investigation session. A user may have
-- at most one session per case (enforced by the unique constraint
-- uq_session_user_case). Sessions begin in ACTIVE status and
-- transition to SUBMITTED when the user finalises their verdict.
--
-- conversation_turns
-- ------------------
-- One row per message turn in an investigation session. Each turn
-- is tagged USER or AI. Ordered by created_at for history replay
-- and prompt assembly.
--
-- Tables NOT touched by this migration:
--   users, cases, submissions
-- ============================================================

-- ────────────────────────────────────────────────────────────
-- 1. investigation_sessions
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS investigation_sessions (
    id          BIGINT      NOT NULL AUTO_INCREMENT,

    -- Foreign keys (plain Long columns; no @ManyToOne JPA joins)
    user_id     BIGINT      NOT NULL,
    case_id     BIGINT      NOT NULL,

    -- Lifecycle status (ACTIVE | SUBMITTED)
    status      VARCHAR(20) NOT NULL,

    -- Denormalised turn counter (USER+AI pair = 1 turn).
    -- Incremented by InvestigationServiceImpl on every postMessage call.
    turn_count  INT         NOT NULL DEFAULT 0,

    -- Creation timestamp (set once, never updated)
    created_at  DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    -- Enforce one session per (user, case) pair at the DB level.
    -- The service layer uses find-or-create to avoid hitting this
    -- constraint in normal operation; this is a safety net only.
    CONSTRAINT uq_session_user_case UNIQUE (user_id, case_id),

    -- Foreign key constraints
    CONSTRAINT fk_session_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_session_case
        FOREIGN KEY (case_id) REFERENCES cases (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Index to support the common lookup pattern:
    -- findActiveSessionByUserAndCase(userId, caseId, status)
    INDEX idx_session_user_case_status (user_id, case_id, status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ────────────────────────────────────────────────────────────
-- 2. conversation_turns
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS conversation_turns (
    id          BIGINT      NOT NULL AUTO_INCREMENT,

    -- FK to the parent investigation_sessions row
    session_id  BIGINT      NOT NULL,

    -- Originator of the turn (USER | AI)
    sender      VARCHAR(10) NOT NULL,

    -- Full text of the turn. TEXT to accommodate long AI replies.
    content     TEXT        NOT NULL,

    -- Creation timestamp — drives chronological ordering
    created_at  DATETIME(6) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_turn_session
        FOREIGN KEY (session_id) REFERENCES investigation_sessions (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Index to support the canonical turn-history query:
    -- findBySessionIdOrderByCreatedAt(sessionId)
    INDEX idx_turn_session_created (session_id, created_at)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

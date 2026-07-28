-- ============================================================
-- V4__create_submissions_table.sql
-- Creates the `submissions` table for the Submission module.
--
-- This migration is owned by the Submission module.
-- Each submission links a user's evidence and reasoning to a
-- specific case. Submissions begin in PENDING status and are
-- transitioned to VERIFIED or REJECTED after evaluation.
--
-- Foreign keys reference:
--   submissions.user_id → users.id
--   submissions.case_id → cases.id
-- ============================================================

CREATE TABLE IF NOT EXISTS submissions (
    id                BIGINT          NOT NULL AUTO_INCREMENT,

    -- Foreign keys (plain Long columns in the JPA entity)
    user_id           BIGINT          NOT NULL,
    case_id           BIGINT          NOT NULL,

    -- Content fields
    title             VARCHAR(255)    NOT NULL,
    description       TEXT            NOT NULL,
    source_name       VARCHAR(255)    NULL,
    source_url        VARCHAR(2048)   NULL,

    -- Enum fields (stored as VARCHAR via EnumType.STRING)
    evidence_type     VARCHAR(20)     NOT NULL,
    confidence_level  VARCHAR(20)     NOT NULL,
    status            VARCHAR(20)     NOT NULL,

    -- Audit timestamps (managed by JPA auditing)
    created_at        DATETIME(6)     NOT NULL,
    updated_at        DATETIME(6)     NOT NULL,

    PRIMARY KEY (id),

    -- Foreign key constraints
    CONSTRAINT fk_submission_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_submission_case
        FOREIGN KEY (case_id) REFERENCES cases (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- Indexes for common query patterns
    INDEX idx_submission_case   (case_id),
    INDEX idx_submission_user   (user_id),
    INDEX idx_submission_status (status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

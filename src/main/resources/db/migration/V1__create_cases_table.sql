-- ============================================================
-- V1__create_cases_table.sql
-- Creates the `cases` table for the Case module.
--
-- This migration is owned by Maajid (Case module).
-- The `cases` table stores all investigable claims.
-- Only rows with status = 'PUBLISHED' are surfaced through
-- the public API. All other rows are internal-only.
-- ============================================================

CREATE TABLE IF NOT EXISTS cases (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    claim                   TEXT            NOT NULL,
    public_evidence_summary TEXT            NOT NULL,

    -- Internal-shape fields. NEVER exposed via public API responses.
    ground_truth            TEXT            NOT NULL,
    ground_truth_explanation TEXT           NOT NULL,
    trusted_references      TEXT            NOT NULL,
    investigation_hints     TEXT            NOT NULL,
    learning_summary        TEXT            NOT NULL,

    status                  VARCHAR(20)     NOT NULL,
    created_at              DATETIME(6)     NOT NULL,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

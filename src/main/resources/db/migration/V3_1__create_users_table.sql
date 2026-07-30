-- ============================================================
-- V3_1__create_users_table.sql
-- Creates the `users` table for the User module.
--
-- This migration is owned by the User module.
-- It ensures that the `users` table exists before the `submissions`
-- table migration (V4) runs, which references `users` via a foreign key.
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id                   BIGINT          NOT NULL AUTO_INCREMENT,
    username             VARCHAR(50)     NOT NULL,
    email                VARCHAR(255)    NOT NULL,
    password_hash        VARCHAR(255)    NOT NULL,
    full_name            VARCHAR(100)    NULL,
    profile_picture_url  VARCHAR(500)    NULL,
    bio                  TEXT            NULL,
    role                 VARCHAR(20)     NOT NULL DEFAULT 'USER',
    account_status       VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    reputation_score     INT             NOT NULL DEFAULT 0,
    created_at           DATETIME(6)     NOT NULL,
    updated_at           DATETIME(6)     NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

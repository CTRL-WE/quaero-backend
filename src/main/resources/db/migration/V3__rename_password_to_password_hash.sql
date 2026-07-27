-- ============================================================
-- V3__rename_password_to_password_hash.sql
--
-- Repairs the `users` table column name: `password` → `password_hash`
--
-- BACKGROUND
-- ----------
-- During the M1-1 audit fix, the JPA entity field was renamed from
-- `password` to `passwordHash` and mapped to column `password_hash`.
-- Hibernate's ddl-auto=update added a new nullable `password_hash`
-- column but did NOT drop the old `password` (NOT NULL) column.
-- Every INSERT then fails because `password` is NOT NULL with no value.
--
-- This migration fixes the schema regardless of which state the DB is in:
--
--   State A (most common): table has `password` (NOT NULL) + maybe an
--     empty nullable `password_hash` added by Hibernate update.
--     Fix: drop the empty `password_hash` if present, then rename
--     `password` → `password_hash`.
--
--   State B (fresh install after code change): table has `password_hash`
--     correctly and NO `password` column — nothing to do.
--
-- The procedure below uses information_schema to detect which state
-- applies and acts accordingly.
-- ============================================================

DROP PROCEDURE IF EXISTS fix_users_password_column;

DELIMITER $$

CREATE PROCEDURE fix_users_password_column()
BEGIN
    DECLARE has_old_col  INT DEFAULT 0;
    DECLARE has_new_col  INT DEFAULT 0;

    -- Check whether the OLD `password` column still exists
    SELECT COUNT(*) INTO has_old_col
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'users'
      AND COLUMN_NAME  = 'password';

    -- Check whether a (possibly blank) `password_hash` column already exists
    SELECT COUNT(*) INTO has_new_col
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME   = 'users'
      AND COLUMN_NAME  = 'password_hash';

    IF has_old_col = 1 THEN
        -- State A: the old column is present

        IF has_new_col = 1 THEN
            -- Hibernate already added an empty password_hash — drop it first
            ALTER TABLE users DROP COLUMN password_hash;
        END IF;

        -- Rename password → password_hash (preserves all existing hashes)
        ALTER TABLE users
            CHANGE COLUMN `password` `password_hash` VARCHAR(255) NOT NULL;

    END IF;
    -- State B (has_old_col = 0): column is already password_hash — nothing to do
END$$

DELIMITER ;

CALL fix_users_password_column();

DROP PROCEDURE IF EXISTS fix_users_password_column;

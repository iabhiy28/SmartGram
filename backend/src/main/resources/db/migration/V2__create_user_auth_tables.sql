-- ============================================================================
-- V2: User & Authentication Tables
-- ============================================================================
-- This migration creates the identity layer for GramConnect. Every person
-- who interacts with the platform has a row in the `users` table.
--
-- KEY DESIGN DECISIONS:
--
-- 1. SINGLE USERS TABLE WITH ROLE COLUMN (vs. separate tables per role):
--    We use one `users` table with a `role` VARCHAR column instead of
--    separate `villagers`, `providers`, `employers` tables.
--
--    WHY: In rural India, the same person often plays multiple roles.
--    A farmer who owns a tractor is both an EMPLOYER (hiring workers)
--    and effectively an equipment owner. A single table simplifies
--    authentication, profile management, and foreign key references.
--
--    The role determines which features are accessible, NOT which table
--    stores the user. Role-specific data lives in linked profile tables
--    (e.g., `service_provider_profiles` in V3).
--
-- 2. PHONE NUMBER AS PRIMARY IDENTIFIER (not email):
--    In rural India, smartphone penetration far exceeds email usage.
--    Phone number is the natural login credential. Email is optional.
--
-- 3. DEMOGRAPHIC FIELDS ON THE USER TABLE:
--    Fields like date_of_birth, occupation, annual_income, caste_category,
--    and land_ownership exist because the Government Scheme Screening
--    Engine (Module 8) needs these to match users against scheme
--    eligibility criteria. These are optional and user-provided.
--
-- 4. REFRESH TOKEN STORAGE:
--    Refresh tokens are stored as SHA-256 hashes, NOT as plaintext.
--    If the database is compromised, stolen token hashes cannot be used
--    to forge valid refresh requests. This is the same principle behind
--    storing password hashes instead of plaintext passwords.
-- ============================================================================

-- ============================================================================
-- USERS
-- ============================================================================
CREATE TABLE users (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Authentication credentials
    phone_number        VARCHAR(15)  NOT NULL,
    email               VARCHAR(100),
    password_hash       VARCHAR(255) NOT NULL,

    -- Identity
    full_name           VARCHAR(100) NOT NULL,
    role                VARCHAR(30)  NOT NULL,
    village_id          UUID,

    -- Demographics (for government scheme eligibility screening)
    date_of_birth       DATE,
    gender              VARCHAR(10),
    occupation          VARCHAR(50),
    annual_income       DECIMAL(12,2),
    caste_category      VARCHAR(30),
    land_ownership      BOOLEAN      DEFAULT FALSE,
    aadhaar_last_four   VARCHAR(4),

    -- Profile
    profile_image_url   VARCHAR(500),
    language_preference VARCHAR(5)   NOT NULL DEFAULT 'en',
    bio                 TEXT,

    -- Account status
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    is_verified         BOOLEAN      NOT NULL DEFAULT FALSE,
    last_login_at       TIMESTAMP WITH TIME ZONE,

    -- Timestamps
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_users_phone        UNIQUE (phone_number),
    CONSTRAINT uk_users_email        UNIQUE (email),
    CONSTRAINT fk_users_village      FOREIGN KEY (village_id) REFERENCES villages(id) ON DELETE SET NULL,
    CONSTRAINT chk_users_role        CHECK (role IN (
        'ROLE_VILLAGER',
        'ROLE_SERVICE_PROVIDER',
        'ROLE_EMPLOYER',
        'ROLE_PANCHAYAT_ADMIN',
        'ROLE_SUPER_ADMIN'
    )),
    CONSTRAINT chk_users_gender      CHECK (gender IS NULL OR gender IN ('MALE', 'FEMALE', 'OTHER')),
    CONSTRAINT chk_users_caste       CHECK (caste_category IS NULL OR caste_category IN (
        'GENERAL', 'OBC', 'SC', 'ST', 'EWS'
    )),
    CONSTRAINT chk_users_lang        CHECK (language_preference IN ('en', 'hi', 'kn'))
);

COMMENT ON TABLE users IS 'Central identity table — every platform user regardless of role';
COMMENT ON COLUMN users.phone_number IS 'Primary login credential — rural India has higher phone than email penetration';
COMMENT ON COLUMN users.password_hash IS 'BCrypt-hashed password — NEVER store plaintext';
COMMENT ON COLUMN users.role IS 'Spring Security granted authority — determines feature access';
COMMENT ON COLUMN users.annual_income IS 'Self-declared income for scheme eligibility screening — NOT verified';
COMMENT ON COLUMN users.caste_category IS 'Required for matching government welfare schemes — user-provided';
COMMENT ON COLUMN users.aadhaar_last_four IS 'Last 4 digits only — NEVER store full Aadhaar numbers (privacy + legal compliance)';

-- ============================================================================
-- REFRESH TOKENS
-- ============================================================================
-- Stores hashed refresh tokens for JWT token rotation.
--
-- WHY A SEPARATE TABLE (vs. a column on users):
--   A user may have multiple active sessions (phone + shared family computer).
--   Each session has its own refresh token. A separate table supports this
--   naturally with a one-to-many relationship.
--
-- WHY HASH THE TOKEN:
--   Refresh tokens are long-lived (7 days). If an attacker gains read access
--   to this table (SQL injection, backup theft), they should NOT be able to
--   use the stolen data to authenticate. SHA-256 hashing provides this.
--
-- REFRESH TOKEN ROTATION (RTR):
--   When a client uses a refresh token to get a new access token:
--   1. The consumed refresh token is immediately marked is_revoked = true
--   2. A new refresh token is issued
--   If a revoked token is reused, ALL tokens for that user are revoked
--   (automatic breach detection — someone stole the old token).
-- ============================================================================
CREATE TABLE refresh_tokens (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    token_hash  VARCHAR(255) NOT NULL,
    device_info VARCHAR(200),
    expires_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    is_revoked  BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_refresh_tokens_hash UNIQUE (token_hash)
);

COMMENT ON TABLE refresh_tokens IS 'Hashed JWT refresh tokens — supports multiple sessions per user and breach detection via RTR';
COMMENT ON COLUMN refresh_tokens.token_hash IS 'SHA-256 hash of the actual token — plaintext token is NEVER stored';
COMMENT ON COLUMN refresh_tokens.device_info IS 'Optional device fingerprint for session management UI';

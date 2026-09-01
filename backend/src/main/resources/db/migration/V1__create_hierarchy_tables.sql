-- ============================================================================
-- V1: Administrative Hierarchy Tables
-- ============================================================================
-- GramConnect organizes India's rural administrative structure as a strict
-- relational hierarchy:
--
--   State → District → Panchayat (Gram Panchayat) → Village
--
-- WHY THIS DESIGN:
-- 1. Every user, complaint, job, service listing, and announcement is scoped
--    to a specific village or panchayat. Without this hierarchy, we cannot
--    filter data by geography.
-- 2. Separate normalized tables (instead of denormalized columns on each row)
--    prevent data anomalies. If "Karnataka" is renamed, we update ONE row.
-- 3. UUIDs as primary keys avoid sequential ID guessing (security) and
--    simplify distributed ID generation if we ever shard the database.
-- 4. UNIQUE constraints on (parent_id, name) prevent duplicate children
--    within the same parent (e.g., two "Bengaluru" districts in Karnataka).
-- ============================================================================

-- Enable UUID generation function (built into PostgreSQL 13+)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- STATES
-- ============================================================================
-- Represents Indian states and union territories.
-- This is the top-level of our hierarchy.
--
-- Design decisions:
--   • `code` is a short unique identifier (e.g., 'KA' for Karnataka)
--     useful for URL slugs and API filtering without exposing UUIDs.
--   • We track created_at/updated_at on EVERY table for debugging,
--     audit trails, and cache invalidation.
-- ============================================================================
CREATE TABLE states (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(10)  NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_states_code UNIQUE (code),
    CONSTRAINT uk_states_name UNIQUE (name)
);

COMMENT ON TABLE states IS 'Indian states and union territories — top of the administrative hierarchy';
COMMENT ON COLUMN states.code IS 'Short unique code, e.g., KA, MH, TN — used for URL slugs and API filtering';

-- ============================================================================
-- DISTRICTS
-- ============================================================================
-- Each state contains multiple districts.
--
-- Design decisions:
--   • Foreign key to states with ON DELETE RESTRICT: we never want to
--     accidentally cascade-delete an entire state's worth of data.
--   • UNIQUE(state_id, name) prevents two districts with the same name
--     within the same state, but allows "Raichur" in both Karnataka
--     and Andhra Pradesh if that ever happened.
-- ============================================================================
CREATE TABLE districts (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    state_id    UUID        NOT NULL,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(20),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_districts_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE RESTRICT,
    CONSTRAINT uk_districts_state_name UNIQUE (state_id, name)
);

COMMENT ON TABLE districts IS 'Administrative districts within a state';

-- ============================================================================
-- PANCHAYATS (Gram Panchayats)
-- ============================================================================
-- Each district contains multiple gram panchayats — the smallest elected
-- administrative body in rural India.
--
-- Design decisions:
--   • office_address is TEXT (nullable) because not all panchayats have
--     a known or digitized office address initially.
--   • This is the primary administrative unit for complaint assignment,
--     announcement publishing, and emergency contact management.
-- ============================================================================
CREATE TABLE panchayats (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    district_id     UUID        NOT NULL,
    name            VARCHAR(100) NOT NULL,
    office_address  TEXT,
    contact_phone   VARCHAR(20),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_panchayats_district FOREIGN KEY (district_id) REFERENCES districts(id) ON DELETE RESTRICT,
    CONSTRAINT uk_panchayats_district_name UNIQUE (district_id, name)
);

COMMENT ON TABLE panchayats IS 'Gram Panchayats — the smallest elected rural administrative body';

-- ============================================================================
-- VILLAGES
-- ============================================================================
-- Each panchayat contains one or more villages. This is the leaf node of
-- our hierarchy and the primary scoping unit for most features.
--
-- Design decisions:
--   • latitude/longitude stored as DECIMAL(9,6) — gives ~0.11 meter
--     precision which is more than sufficient for village-center coordinates.
--   • pin_code is VARCHAR(6) not INT because leading zeros matter
--     (e.g., PIN 010101) and we never do arithmetic on postal codes.
--   • population is nullable — we may not have census data for all villages
--     at registration time.
-- ============================================================================
CREATE TABLE villages (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    panchayat_id    UUID        NOT NULL,
    name            VARCHAR(100) NOT NULL,
    pin_code        VARCHAR(6)  NOT NULL,
    latitude        DECIMAL(9,6),
    longitude       DECIMAL(9,6),
    population      INT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_villages_panchayat FOREIGN KEY (panchayat_id) REFERENCES panchayats(id) ON DELETE RESTRICT,
    CONSTRAINT uk_villages_panchayat_name UNIQUE (panchayat_id, name)
);

COMMENT ON TABLE villages IS 'Individual villages — the leaf node where users, listings, and complaints are scoped';
COMMENT ON COLUMN villages.latitude IS 'Village center latitude — DECIMAL(9,6) gives ~0.11m precision';
COMMENT ON COLUMN villages.longitude IS 'Village center longitude';
COMMENT ON COLUMN villages.pin_code IS 'Indian postal PIN code — stored as VARCHAR to preserve leading zeros';

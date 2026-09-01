-- ============================================================================
-- V3: Services Marketplace Tables
-- ============================================================================
-- This is the local services directory — the core discovery feature of
-- GramConnect. It connects villagers who need work done (plumbing, electrical,
-- carpentry) with local service providers.
--
-- ENTITY RELATIONSHIPS:
--
--   User (ROLE_SERVICE_PROVIDER)
--     └── ServiceProviderProfile (1:1)
--           └── ServiceOffering (1:many)
--                 └── ServiceBooking (1:many)
--
-- KEY DESIGN DECISIONS:
--
-- 1. SEPARATE service_categories TABLE (vs. hardcoded ENUM):
--    Categories like "Electrician", "Plumber", "Carpenter" are stored in a
--    reference table rather than as a CHECK constraint or Java enum.
--    WHY: The Super Admin needs to add/modify/deactivate categories at
--    runtime without deploying a new database migration.
--
-- 2. DENORMALIZED average_rating ON service_provider_profiles:
--    Instead of computing AVG(rating) from the reviews table on every
--    search query (which is O(reviews) per provider × N providers per page),
--    we maintain a pre-computed average_rating and total_reviews count.
--    This is updated transactionally when a new review is submitted.
--    WHY: Search results showing 20 providers should not trigger 20
--    subqueries into the reviews table. This is a classic read-optimization
--    trade-off — slightly more work on writes, massively faster reads.
--
-- 3. BOOKING STATUS STATE MACHINE:
--    Status transitions are constrained in the application layer:
--      REQUESTED → ACCEPTED | DECLINED
--      ACCEPTED  → IN_PROGRESS | CANCELLED
--      IN_PROGRESS → COMPLETED | CANCELLED
--    The database CHECK constraint ensures only valid status values exist,
--    but the STATE TRANSITION RULES are enforced in the Java service layer
--    (not in the database). This keeps business logic testable and explicit.
-- ============================================================================

-- ============================================================================
-- SERVICE CATEGORIES
-- ============================================================================
CREATE TABLE service_categories (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(50)  NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    description     TEXT,
    icon            VARCHAR(50),
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    display_order   INT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_service_categories_name UNIQUE (name)
);

COMMENT ON TABLE service_categories IS 'Master list of service types — managed by Super Admin at runtime';
COMMENT ON COLUMN service_categories.name IS 'Machine-readable key (e.g., ELECTRICIAN) — used in API filtering';
COMMENT ON COLUMN service_categories.display_name IS 'Human-readable label (e.g., "Electrician") — shown in UI';
COMMENT ON COLUMN service_categories.icon IS 'Icon identifier for frontend rendering (e.g., Lucide icon name)';

-- ============================================================================
-- SERVICE PROVIDER PROFILES
-- ============================================================================
-- Extended profile data for users with ROLE_SERVICE_PROVIDER.
-- This is a 1:1 extension of the users table — NOT a replacement.
-- The user's name, phone, village come from the users table.
-- This table holds provider-specific attributes.
-- ============================================================================
CREATE TABLE service_provider_profiles (
    id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID        NOT NULL,
    bio                     TEXT,
    experience_years        INT         DEFAULT 0,
    service_radius_km       INT         NOT NULL DEFAULT 10,
    is_available            BOOLEAN     NOT NULL DEFAULT TRUE,

    -- Verification
    id_proof_url            VARCHAR(500),
    skill_certificate_url   VARCHAR(500),
    verification_status     VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verified_by_id          UUID,
    verified_at             TIMESTAMP WITH TIME ZONE,

    -- Denormalized rating aggregates (updated on each new review)
    average_rating          DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    total_reviews           INT         NOT NULL DEFAULT 0,
    total_completed_jobs    INT         NOT NULL DEFAULT 0,

    -- Timestamps
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_provider_user       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_provider_user       UNIQUE (user_id),
    CONSTRAINT fk_provider_verifier   FOREIGN KEY (verified_by_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_provider_verification CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')),
    CONSTRAINT chk_provider_radius    CHECK (service_radius_km > 0 AND service_radius_km <= 100),
    CONSTRAINT chk_provider_rating    CHECK (average_rating >= 0.00 AND average_rating <= 5.00)
);

COMMENT ON TABLE service_provider_profiles IS '1:1 extension of users for ROLE_SERVICE_PROVIDER — contains provider-specific attributes';
COMMENT ON COLUMN service_provider_profiles.average_rating IS 'Pre-computed average — avoids expensive JOIN+AVG on every search query';
COMMENT ON COLUMN service_provider_profiles.verification_status IS 'Panchayat Admin must verify providers before they appear in search results';

-- ============================================================================
-- SERVICE OFFERINGS
-- ============================================================================
-- What a provider actually offers. One provider may offer multiple services
-- (e.g., a handyman does both plumbing and electrical work).
-- ============================================================================
CREATE TABLE service_offerings (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id     UUID        NOT NULL,
    category_id     UUID        NOT NULL,
    description     TEXT,
    base_price      DECIMAL(10,2),
    price_unit      VARCHAR(20) NOT NULL DEFAULT 'FIXED',
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_offerings_provider  FOREIGN KEY (provider_id) REFERENCES service_provider_profiles(id) ON DELETE CASCADE,
    CONSTRAINT fk_offerings_category  FOREIGN KEY (category_id) REFERENCES service_categories(id) ON DELETE RESTRICT,
    CONSTRAINT uk_offerings_provider_category UNIQUE (provider_id, category_id),
    CONSTRAINT chk_offerings_price_unit CHECK (price_unit IN ('PER_HOUR', 'PER_DAY', 'FIXED', 'NEGOTIABLE')),
    CONSTRAINT chk_offerings_base_price CHECK (base_price IS NULL OR base_price >= 0)
);

COMMENT ON TABLE service_offerings IS 'Specific services a provider offers — one per category per provider';
COMMENT ON COLUMN service_offerings.base_price IS 'Starting price — may differ from final negotiated price on the booking';

-- ============================================================================
-- SERVICE BOOKINGS
-- ============================================================================
-- Tracks the lifecycle of a service request from a villager to a provider.
--
-- STATE MACHINE (enforced in Java, not in SQL):
--   REQUESTED  →  ACCEPTED  →  IN_PROGRESS  →  COMPLETED
--       ↓            ↓             ↓
--    (timeout)    CANCELLED     CANCELLED
--       ↓
--    DECLINED
-- ============================================================================
CREATE TABLE service_bookings (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    villager_id         UUID        NOT NULL,
    offering_id         UUID        NOT NULL,
    provider_id         UUID        NOT NULL,

    -- Booking details
    status              VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    scheduled_date      DATE,
    scheduled_time_slot VARCHAR(50),
    address_notes       TEXT,
    problem_description TEXT,

    -- Financials
    quoted_price        DECIMAL(10,2),
    final_price         DECIMAL(10,2),

    -- Lifecycle timestamps
    accepted_at         TIMESTAMP WITH TIME ZONE,
    started_at          TIMESTAMP WITH TIME ZONE,
    completed_at        TIMESTAMP WITH TIME ZONE,
    cancelled_at        TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,

    -- Timestamps
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bookings_villager   FOREIGN KEY (villager_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_offering   FOREIGN KEY (offering_id) REFERENCES service_offerings(id) ON DELETE RESTRICT,
    CONSTRAINT fk_bookings_provider   FOREIGN KEY (provider_id) REFERENCES service_provider_profiles(id) ON DELETE RESTRICT,
    CONSTRAINT chk_bookings_status    CHECK (status IN (
        'REQUESTED', 'ACCEPTED', 'DECLINED', 'IN_PROGRESS',
        'COMPLETED', 'CANCELLED'
    ))
);

COMMENT ON TABLE service_bookings IS 'Full lifecycle of a service request — from villager request to completion and review';
COMMENT ON COLUMN service_bookings.quoted_price IS 'Price quoted by the provider upon acceptance';
COMMENT ON COLUMN service_bookings.final_price IS 'Actual price charged after job completion — may differ from quote';

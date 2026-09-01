-- ============================================================================
-- V4: Agricultural Equipment Rental Tables
-- ============================================================================
-- This module powers the agricultural equipment sharing marketplace.
-- In rural India, most farmers cannot afford to own expensive machinery
-- like tractors, harvesters, or rotavators. They rent from neighbors
-- or local equipment owners during planting/harvesting seasons.
--
-- CRITICAL ENGINEERING CHALLENGE — DOUBLE BOOKING PREVENTION:
--
-- During harvest season, dozens of farmers may try to book the same
-- tractor for overlapping dates. Without proper concurrency controls,
-- two farmers could both receive "Booking Confirmed" for the same
-- tractor on the same day.
--
-- SOLUTION ARCHITECTURE:
--
-- 1. DATABASE-LEVEL: Date range overlap detection query
--    The booking service checks for existing CONFIRMED/ACTIVE bookings
--    whose date ranges overlap with the requested dates:
--
--    SELECT COUNT(*) FROM equipment_bookings
--    WHERE equipment_id = :equipmentId
--      AND status IN ('CONFIRMED', 'ACTIVE')
--      AND start_date <= :requestedEnd
--      AND end_date >= :requestedStart
--    FOR UPDATE;  -- <== THIS IS THE KEY
--
--    The FOR UPDATE clause acquires a PESSIMISTIC WRITE LOCK on the
--    matching rows, blocking other transactions from reading these rows
--    until the current transaction commits or rolls back.
--
-- 2. APPLICATION-LEVEL: @Transactional with REPEATABLE_READ isolation
--    The booking service method runs inside a transaction with
--    Isolation.REPEATABLE_READ. This prevents phantom reads — if
--    another transaction inserts a conflicting booking between our
--    overlap check and our INSERT, our transaction will detect the
--    conflict and throw a serialization error.
--
-- 3. DEFENSE-IN-DEPTH: Unique functional constraint
--    We do NOT add a database-level unique constraint on date ranges
--    because SQL doesn't natively support "no overlapping ranges"
--    constraints (PostgreSQL has exclusion constraints via btree_gist
--    extension, which we could use in V2 for stronger guarantees).
--    For MVP, the pessimistic lock + application logic is sufficient.
-- ============================================================================

-- ============================================================================
-- EQUIPMENT CATEGORIES
-- ============================================================================
CREATE TABLE equipment_categories (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(50)  NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    description     TEXT,
    icon            VARCHAR(50),
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    display_order   INT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_equipment_categories_name UNIQUE (name)
);

COMMENT ON TABLE equipment_categories IS 'Types of agricultural equipment — Tractor, Rotavator, Harvester, etc.';

-- ============================================================================
-- EQUIPMENT LISTINGS
-- ============================================================================
CREATE TABLE equipment (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id            UUID        NOT NULL,
    village_id          UUID        NOT NULL,
    category_id         UUID        NOT NULL,

    -- Equipment details
    title               VARCHAR(150) NOT NULL,
    description         TEXT,
    make                VARCHAR(100),
    model               VARCHAR(100),
    year_of_purchase    INT,
    horse_power         INT,

    -- Pricing
    hourly_rate         DECIMAL(10,2),
    daily_rate          DECIMAL(10,2),

    -- Photos (stored as JSON array of URLs for simplicity)
    -- In a larger system, we would use a separate equipment_photos table
    photo_urls          JSONB       NOT NULL DEFAULT '[]'::jsonb,

    -- Availability & Location
    is_operational      BOOLEAN     NOT NULL DEFAULT TRUE,
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    latitude            DECIMAL(9,6),
    longitude           DECIMAL(9,6),
    service_radius_km   INT         NOT NULL DEFAULT 15,

    -- Timestamps
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipment_owner     FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_equipment_village   FOREIGN KEY (village_id) REFERENCES villages(id) ON DELETE RESTRICT,
    CONSTRAINT fk_equipment_category  FOREIGN KEY (category_id) REFERENCES equipment_categories(id) ON DELETE RESTRICT,
    CONSTRAINT chk_equipment_radius   CHECK (service_radius_km > 0 AND service_radius_km <= 100),
    CONSTRAINT chk_equipment_rates    CHECK (
        hourly_rate IS NULL OR hourly_rate >= 0
    )
);

COMMENT ON TABLE equipment IS 'Agricultural equipment available for rental — tractors, harvesters, pumps, etc.';
COMMENT ON COLUMN equipment.photo_urls IS 'JSONB array of image URLs — simpler than a join table for MVP';
COMMENT ON COLUMN equipment.service_radius_km IS 'Maximum distance the owner is willing to transport the equipment';

-- ============================================================================
-- EQUIPMENT BOOKINGS
-- ============================================================================
-- Each booking represents a reservation of equipment for a date range.
--
-- IMPORTANT: The overlap prevention logic is in the Java service layer,
-- NOT purely in SQL constraints. See the file header comments for details.
--
-- STATE MACHINE:
--   PENDING   →  CONFIRMED  →  ACTIVE  →  COMPLETED
--       ↓           ↓           ↓
--   REJECTED    CANCELLED    CANCELLED
--
-- PENDING:    Farmer has requested, owner hasn't responded yet
-- CONFIRMED:  Owner has accepted the request (dates are now LOCKED)
-- ACTIVE:     Equipment is currently being used (start_date <= today)
-- COMPLETED:  Rental period is over and equipment has been returned
-- CANCELLED:  Either party cancelled before or during the rental
-- REJECTED:   Owner declined the request
-- ============================================================================
CREATE TABLE equipment_bookings (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id    UUID        NOT NULL,
    renter_id       UUID        NOT NULL,

    -- Booking dates
    start_date      DATE        NOT NULL,
    end_date        DATE        NOT NULL,

    -- Pricing
    rate_type       VARCHAR(10) NOT NULL DEFAULT 'DAILY',
    rate_amount     DECIMAL(10,2) NOT NULL,
    total_days      INT,
    total_amount    DECIMAL(10,2),

    -- Status
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING',

    -- Notes & Communication
    renter_notes    TEXT,
    owner_notes     TEXT,
    cancellation_reason TEXT,

    -- Timestamps
    confirmed_at    TIMESTAMP WITH TIME ZONE,
    cancelled_at    TIMESTAMP WITH TIME ZONE,
    completed_at    TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equip_booking_equipment  FOREIGN KEY (equipment_id) REFERENCES equipment(id) ON DELETE RESTRICT,
    CONSTRAINT fk_equip_booking_renter     FOREIGN KEY (renter_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_equip_booking_dates     CHECK (end_date >= start_date),
    CONSTRAINT chk_equip_booking_status    CHECK (status IN (
        'PENDING', 'CONFIRMED', 'ACTIVE', 'COMPLETED', 'CANCELLED', 'REJECTED'
    )),
    CONSTRAINT chk_equip_booking_rate_type CHECK (rate_type IN ('HOURLY', 'DAILY'))
);

COMMENT ON TABLE equipment_bookings IS 'Equipment reservations with concurrency-safe date-range booking';
COMMENT ON COLUMN equipment_bookings.status IS 'State machine enforced in Java — DB only validates allowed values';
COMMENT ON COLUMN equipment_bookings.total_days IS 'Computed as (end_date - start_date + 1) by the service layer';

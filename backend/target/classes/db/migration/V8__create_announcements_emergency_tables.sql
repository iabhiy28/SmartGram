-- ============================================================================
-- V8: Announcements & Emergency Contact Tables
-- ============================================================================
-- Two critical features for village communication:
--
-- 1. ANNOUNCEMENTS: Official broadcasts from Panchayat administrators
--    to all residents of their jurisdiction.
--
-- 2. EMERGENCY CONTACTS: A verified directory of essential services
--    (ambulance, police, fire, hospitals, utility departments).
--
-- KEY DESIGN DECISIONS:
--
-- 1. ANNOUNCEMENT SCOPING (Panchayat-wide vs. Village-specific):
--    An announcement has both panchayat_id (required) and village_id
--    (optional). If village_id is NULL, it's a panchayat-wide broadcast
--    visible to ALL villages under that panchayat. If village_id is set,
--    it targets only that specific village.
--    WHY: Water supply schedules might affect the entire panchayat,
--    while a village meeting only concerns one village.
--
-- 2. PRIORITY LEVELS for announcements:
--    NORMAL     → routine information (meeting notices, schedules)
--    IMPORTANT  → requires attention (policy changes, deadlines)
--    EMERGENCY  → urgent safety alerts (flood warning, disease outbreak)
--    Emergency announcements trigger immediate push notifications to
--    all connected WebSocket clients.
--
-- 3. EXPIRY for announcements:
--    expires_at allows time-bound announcements. A "water shutdown from
--    2 PM to 8 PM today" announcement becomes irrelevant after 8 PM.
--    The frontend filters out expired announcements. A background job
--    can periodically archive them.
--
-- 4. VERIFIED EMERGENCY CONTACTS:
--    The is_verified flag indicates that a Panchayat Admin has confirmed
--    the contact number is currently active. last_verified_at tracks when
--    this was last checked. This prevents showing disconnected or
--    reassigned numbers to citizens during genuine emergencies.
-- ============================================================================

-- ============================================================================
-- ANNOUNCEMENTS
-- ============================================================================
CREATE TABLE announcements (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    panchayat_id    UUID        NOT NULL,
    village_id      UUID,
    publisher_id    UUID        NOT NULL,

    -- Content
    title           VARCHAR(200) NOT NULL,
    content         TEXT        NOT NULL,
    category        VARCHAR(50) NOT NULL,
    priority        VARCHAR(20) NOT NULL DEFAULT 'NORMAL',

    -- Media
    attachment_url  VARCHAR(500),
    image_url       VARCHAR(500),

    -- Lifecycle
    expires_at      TIMESTAMP WITH TIME ZONE,
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,

    -- Timestamps
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_announcements_panchayat FOREIGN KEY (panchayat_id) REFERENCES panchayats(id) ON DELETE RESTRICT,
    CONSTRAINT fk_announcements_village   FOREIGN KEY (village_id) REFERENCES villages(id) ON DELETE RESTRICT,
    CONSTRAINT fk_announcements_publisher FOREIGN KEY (publisher_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_announcements_priority CHECK (priority IN ('NORMAL', 'IMPORTANT', 'EMERGENCY')),
    CONSTRAINT chk_announcements_category CHECK (category IN (
        'WATER_SUPPLY', 'ELECTRICITY', 'MEETING', 'HEALTH_CAMP',
        'VACCINATION', 'GOVERNMENT_CAMP', 'SCHOOL', 'LOCAL_EVENT',
        'EMERGENCY', 'GENERAL'
    ))
);

COMMENT ON TABLE announcements IS 'Official broadcasts from Panchayat admins to village residents';
COMMENT ON COLUMN announcements.village_id IS 'NULL = panchayat-wide broadcast; set = specific village only';
COMMENT ON COLUMN announcements.expires_at IS 'Auto-archive after this time — irrelevant announcements are hidden';

-- ============================================================================
-- EMERGENCY CONTACTS
-- ============================================================================
CREATE TABLE emergency_contacts (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    panchayat_id        UUID        NOT NULL,

    -- Contact details
    service_name        VARCHAR(100) NOT NULL,
    category            VARCHAR(50) NOT NULL,
    contact_person      VARCHAR(100),
    phone_number        VARCHAR(20) NOT NULL,
    alternate_phone     VARCHAR(20),
    address             TEXT,
    operating_hours     VARCHAR(100),

    -- Verification
    is_verified         BOOLEAN     NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    last_verified_at    DATE,
    verified_by_id      UUID,

    -- Display
    display_order       INT         NOT NULL DEFAULT 0,

    -- Timestamps
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_emergency_panchayat  FOREIGN KEY (panchayat_id) REFERENCES panchayats(id) ON DELETE RESTRICT,
    CONSTRAINT fk_emergency_verifier   FOREIGN KEY (verified_by_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_emergency_category  CHECK (category IN (
        'AMBULANCE', 'POLICE', 'FIRE', 'HOSPITAL', 'PHC',
        'ELECTRICITY', 'WATER', 'PANCHAYAT', 'VETERINARY',
        'WOMEN_HELPLINE', 'CHILD_HELPLINE', 'OTHER'
    ))
);

COMMENT ON TABLE emergency_contacts IS 'Verified emergency service directory — managed by Panchayat admins';
COMMENT ON COLUMN emergency_contacts.is_verified IS 'Admin-confirmed that this number is active and correct';
COMMENT ON COLUMN emergency_contacts.last_verified_at IS 'When was this contact last confirmed — stale contacts should be re-verified';

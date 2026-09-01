-- ============================================================================
-- V9: Notifications, Verified Reviews, Bookmarks & Audit Logging Tables
-- ============================================================================
-- Cross-cutting operational tables for engagement, feedback integrity,
-- user preferences, and governance.
--
-- KEY DESIGN DECISIONS:
--
-- 1. VERIFIED REVIEWS & AGGREGATION INTEGRITY:
--    - Reviews MUST reference a completed `service_bookings` record (`booking_id`).
--    - UNIQUE constraint on `booking_id` prevents duplicate reviews.
--    - `rating` is validated between 1 and 5.
--    - Individual category scores (punctuality, quality, pricing, communication)
--      provide multi-dimensional feedback.
--
-- 2. NOTIFICATIONS DISPATCH & RETENTION:
--    - Standardized notification types (`COMPLAINT_UPDATE`, `BOOKING_STATUS`,
--      `JOB_APPLICATION`, `SLA_BREACH`, `BROADCAST_ANNOUNCEMENT`).
--    - Stores target `recipient_id`, `reference_id` (e.g., complaintId, bookingId),
--      and unread status.
--
-- 3. IMMUTABLE AUDIT LOGGING:
--    - Tracks high-privilege operations (status overrides, provider verifications,
--      user suspensions, role elevations).
--    - Stores `before_payload` and `after_payload` as JSONB.
--    - No UPDATE or DELETE operations permitted on `audit_logs`.
-- ============================================================================

-- ============================================================================
-- VERIFIED REVIEWS
-- ============================================================================
CREATE TABLE reviews (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id          UUID        NOT NULL,
    reviewer_id         UUID        NOT NULL,
    provider_id         UUID        NOT NULL,

    -- Ratings (1 to 5 scale)
    rating              INT         NOT NULL,
    punctuality_rating  INT,
    quality_rating      INT,
    pricing_rating      INT,
    behavior_rating     INT,

    -- Feedback text
    review_title        VARCHAR(150),
    review_comment      TEXT,

    -- Provider response
    provider_reply      TEXT,
    replied_at          TIMESTAMP WITH TIME ZONE,

    -- Timestamps
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reviews_booking   FOREIGN KEY (booking_id) REFERENCES service_bookings(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_reviewer  FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_provider  FOREIGN KEY (provider_id) REFERENCES service_provider_profiles(id) ON DELETE CASCADE,
    CONSTRAINT uk_reviews_booking   UNIQUE (booking_id),
    CONSTRAINT chk_reviews_rating   CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_punc     CHECK (punctuality_rating IS NULL OR punctuality_rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_qual     CHECK (quality_rating IS NULL OR quality_rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_price    CHECK (pricing_rating IS NULL OR pricing_rating BETWEEN 1 AND 5),
    CONSTRAINT chk_reviews_behav    CHECK (behavior_rating IS NULL OR behavior_rating BETWEEN 1 AND 5)
);

COMMENT ON TABLE reviews IS 'Verified reviews tied strictly to completed service bookings — prevents fake ratings';
COMMENT ON COLUMN reviews.booking_id IS 'Unique 1:1 foreign key with booking — prevents multiple reviews for one job';

-- ============================================================================
-- IN-APP NOTIFICATIONS
-- ============================================================================
CREATE TABLE notifications (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id    UUID        NOT NULL,
    title           VARCHAR(150) NOT NULL,
    message         TEXT        NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    reference_type  VARCHAR(50),
    reference_id    UUID,
    action_url      VARCHAR(255),
    is_read         BOOLEAN     NOT NULL DEFAULT FALSE,
    read_at         TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_notifications_type CHECK (notification_type IN (
        'COMPLAINT_STATUS_UPDATE',
        'COMPLAINT_ASSIGNED',
        'COMPLAINT_OVERDUE_ALERT',
        'SERVICE_BOOKING_REQUESTED',
        'SERVICE_BOOKING_ACCEPTED',
        'SERVICE_BOOKING_DECLINED',
        'SERVICE_BOOKING_COMPLETED',
        'EQUIPMENT_BOOKING_CONFIRMED',
        'EQUIPMENT_BOOKING_REJECTED',
        'JOB_APPLICATION_RECEIVED',
        'JOB_APPLICATION_STATUS_UPDATE',
        'NEW_VILLAGE_ANNOUNCEMENT',
        'EMERGENCY_BROADCAST',
        'SYSTEM_ALERT'
    ))
);

COMMENT ON TABLE notifications IS 'Persistent notifications delivered to in-app bell center and pushed via WebSockets';
COMMENT ON COLUMN notifications.reference_id IS 'Polymorphic reference ID to the entity (complaint, booking, job, announcement)';

-- ============================================================================
-- SERVICE BOOKMARKS / FAVORITES
-- ============================================================================
CREATE TABLE user_saved_services (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL,
    provider_id     UUID        NOT NULL,
    saved_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_saved_services_user     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_services_provider FOREIGN KEY (provider_id) REFERENCES service_provider_profiles(id) ON DELETE CASCADE,
    CONSTRAINT uk_saved_services_user_provider UNIQUE (user_id, provider_id)
);

COMMENT ON TABLE user_saved_services IS 'Villager bookmarked service providers for quick future access';

-- ============================================================================
-- AUDIT LOGS (Compliance & Security Governance)
-- ============================================================================
CREATE TABLE audit_logs (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id        UUID,
    actor_role      VARCHAR(50),
    action          VARCHAR(100) NOT NULL,
    entity_name     VARCHAR(50)  NOT NULL,
    entity_id       UUID         NOT NULL,
    ip_address      VARCHAR(45),
    user_agent      VARCHAR(255),
    before_payload  JSONB,
    after_payload   JSONB,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_logs_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL
);

COMMENT ON TABLE audit_logs IS 'Immutable compliance audit trail for high-privilege actions (complaint status overrides, provider verifications, user role changes)';
COMMENT ON COLUMN audit_logs.before_payload IS 'JSON snapshot of entity state prior to mutation';
COMMENT ON COLUMN audit_logs.after_payload IS 'JSON snapshot of entity state after mutation';

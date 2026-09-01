-- ============================================================================
-- V6: Civic Complaint Management & SLA Tracking Tables
-- ============================================================================
-- This is arguably the most important module in GramConnect. It allows
-- villagers to report infrastructure problems (broken streetlights,
-- water leaks, road damage) and track their resolution.
--
-- WHY THIS MATTERS:
-- In most villages today, reporting a broken hand pump requires:
-- 1. Walking to the Panchayat office (which may be in another village)
-- 2. Waiting for the administrator to be available
-- 3. Verbally describing the problem
-- 4. Having NO way to track if anything was done about it
--
-- GramConnect digitizes this entire workflow with:
-- - Photo evidence
-- - GPS location tagging
-- - Automated SLA deadlines
-- - Transparent status tracking
-- - Escalation on overdue complaints
--
-- KEY DESIGN DECISIONS:
--
-- 1. TICKET NUMBER (auto-incrementing BIGINT):
--    Alongside the UUID primary key, we generate a human-readable
--    sequential ticket number (e.g., #1042). UUIDs are terrible for
--    humans to remember or communicate verbally ("my complaint number
--    is seven-b-eight-f-nine..."). Ticket numbers let a villager say
--    "I filed complaint number 1042" over the phone.
--
-- 2. COMPLAINT CATEGORY TABLE WITH DEFAULT SLA HOURS:
--    Different problem types have different urgency levels.
--    "Electricity Emergency" should resolve in 4 hours.
--    "Road Damage" might take 7 days.
--    By storing default_sla_hours on the category, the system can
--    automatically compute expected_resolution_at when a complaint
--    is assigned: assigned_at + default_sla_hours.
--
-- 3. SLA BREACH DETECTION (Spring Scheduler):
--    A scheduled job runs every 5 minutes:
--      SELECT * FROM complaints
--      WHERE status NOT IN ('RESOLVED', 'CLOSED')
--        AND expected_resolution_at IS NOT NULL
--        AND expected_resolution_at < NOW()
--        AND is_overdue = FALSE
--    It marks is_overdue = TRUE and fires escalation notifications.
--    This runs as a BACKGROUND JOB, not inside HTTP requests, because:
--    - It needs to run even when no one is using the API
--    - It could process thousands of complaints
--    - HTTP requests should respond in < 200ms, not scan entire tables
--
-- 4. DUPLICATE COMPLAINT DETECTION:
--    The `duplicate_of_id` self-referencing FK allows admins to mark
--    a complaint as a duplicate of another. The `upvote_count` tracks
--    how many citizens reported the same issue — this helps admins
--    prioritize based on community impact.
--
-- 5. COMPLAINT COMMENTS — INTERNAL vs. PUBLIC:
--    The `is_internal` flag on comments allows admins to leave notes
--    visible only to other admins (e.g., "Called PWD contractor, they
--    said they'll come Thursday"). Citizens only see public comments.
-- ============================================================================

-- ============================================================================
-- COMPLAINT CATEGORIES
-- ============================================================================
CREATE TABLE complaint_categories (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(50)  NOT NULL,
    display_name        VARCHAR(100) NOT NULL,
    description         TEXT,
    icon                VARCHAR(50),
    default_sla_hours   INT         NOT NULL DEFAULT 48,
    default_priority    VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    responsible_department VARCHAR(100),
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    display_order       INT         NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_complaint_categories_name UNIQUE (name),
    CONSTRAINT chk_complaint_cat_sla CHECK (default_sla_hours > 0),
    CONSTRAINT chk_complaint_cat_priority CHECK (default_priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

COMMENT ON TABLE complaint_categories IS 'Types of civic issues with default SLA deadlines — drives automated escalation';
COMMENT ON COLUMN complaint_categories.default_sla_hours IS 'Hours to resolve — automatically sets expected_resolution_at on assignment';
COMMENT ON COLUMN complaint_categories.responsible_department IS 'Default department for routing (e.g., PWD, Electricity Board)';

-- ============================================================================
-- COMPLAINTS
-- ============================================================================
CREATE TABLE complaints (
    id                      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_number           BIGINT      GENERATED ALWAYS AS IDENTITY,
    citizen_id              UUID        NOT NULL,
    village_id              UUID        NOT NULL,
    category_id             UUID        NOT NULL,

    -- Issue details
    title                   VARCHAR(200) NOT NULL,
    description             TEXT,
    latitude                DECIMAL(9,6),
    longitude               DECIMAL(9,6),
    address_landmark        VARCHAR(200),

    -- Priority & Status
    priority                VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status                  VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',

    -- Assignment
    assigned_to_id          UUID,
    assigned_department     VARCHAR(100),
    assigned_at             TIMESTAMP WITH TIME ZONE,

    -- SLA tracking
    expected_resolution_at  TIMESTAMP WITH TIME ZONE,
    resolved_at             TIMESTAMP WITH TIME ZONE,
    closed_at               TIMESTAMP WITH TIME ZONE,
    is_overdue              BOOLEAN     NOT NULL DEFAULT FALSE,
    sla_breached_at         TIMESTAMP WITH TIME ZONE,

    -- Resolution
    resolution_notes        TEXT,

    -- Citizen feedback (after resolution)
    citizen_feedback        TEXT,
    citizen_satisfaction_rating INT,

    -- Duplicate management
    duplicate_of_id         UUID,
    upvote_count            INT         NOT NULL DEFAULT 1,

    -- Timestamps
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_complaints_ticket     UNIQUE (ticket_number),
    CONSTRAINT fk_complaints_citizen    FOREIGN KEY (citizen_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_complaints_village    FOREIGN KEY (village_id) REFERENCES villages(id) ON DELETE RESTRICT,
    CONSTRAINT fk_complaints_category   FOREIGN KEY (category_id) REFERENCES complaint_categories(id) ON DELETE RESTRICT,
    CONSTRAINT fk_complaints_assignee   FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_complaints_duplicate  FOREIGN KEY (duplicate_of_id) REFERENCES complaints(id) ON DELETE SET NULL,
    CONSTRAINT chk_complaints_priority  CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_complaints_status    CHECK (status IN (
        'SUBMITTED', 'UNDER_REVIEW', 'ASSIGNED', 'IN_PROGRESS',
        'RESOLVED', 'CLOSED', 'REOPENED'
    )),
    CONSTRAINT chk_complaints_satisfaction CHECK (
        citizen_satisfaction_rating IS NULL OR citizen_satisfaction_rating BETWEEN 1 AND 5
    )
);

COMMENT ON TABLE complaints IS 'Civic issue reports with SLA tracking, assignment, and citizen feedback';
COMMENT ON COLUMN complaints.ticket_number IS 'Human-readable sequential number — easier to communicate than UUIDs';
COMMENT ON COLUMN complaints.is_overdue IS 'Set to TRUE by Spring Scheduler when expected_resolution_at is breached';
COMMENT ON COLUMN complaints.duplicate_of_id IS 'Self-FK — links to the original complaint if this is a duplicate report';
COMMENT ON COLUMN complaints.upvote_count IS 'Number of citizens who reported the same issue — helps admin prioritize';

-- ============================================================================
-- COMPLAINT ATTACHMENTS
-- ============================================================================
-- Photos and documents attached to a complaint.
-- File content is stored in Object Storage (local filesystem or S3).
-- Only the metadata and URL are stored in PostgreSQL.
-- ============================================================================
CREATE TABLE complaint_attachments (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    complaint_id    UUID        NOT NULL,
    file_url        VARCHAR(500) NOT NULL,
    file_name       VARCHAR(200),
    file_type       VARCHAR(50),
    file_size_bytes BIGINT,
    uploaded_by_id  UUID        NOT NULL,
    uploaded_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attachments_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_attachments_uploader  FOREIGN KEY (uploaded_by_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_attachments_size     CHECK (file_size_bytes IS NULL OR file_size_bytes <= 5242880)
);

COMMENT ON TABLE complaint_attachments IS 'Photo/document metadata — actual files stored in Object Storage, NOT in PostgreSQL';
COMMENT ON COLUMN complaint_attachments.file_size_bytes IS 'Max 5MB (5,242,880 bytes) — validated at upload time';

-- ============================================================================
-- COMPLAINT COMMENTS / ACTIVITY LOG
-- ============================================================================
-- Both admins and citizens can add comments to track progress.
-- Internal comments (is_internal = TRUE) are only visible to admins.
-- ============================================================================
CREATE TABLE complaint_comments (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    complaint_id    UUID        NOT NULL,
    author_id       UUID        NOT NULL,
    comment_text    TEXT        NOT NULL,
    is_internal     BOOLEAN     NOT NULL DEFAULT FALSE,
    attachment_url  VARCHAR(500),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_author    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE RESTRICT
);

COMMENT ON TABLE complaint_comments IS 'Progress updates and notes on complaints — supports internal admin-only comments';

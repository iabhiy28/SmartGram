-- ============================================================================
-- V5: Local Jobs & Employment Tables
-- ============================================================================
-- This module creates the informal labor marketplace. In rural India,
-- seasonal agricultural work and small construction projects are the
-- primary sources of daily-wage employment.
--
-- REAL-WORLD CONTEXT:
-- A farmer might need 5 workers for paddy transplanting for 3 days.
-- Today, they walk around the village or send word through relatives.
-- GramConnect formalizes this into a structured job posting with
-- clear wage rates, dates, and an application lifecycle.
--
-- KEY DESIGN DECISIONS:
--
-- 1. WORKER CAPACITY ENFORCEMENT:
--    Each job has `workers_needed` and `workers_accepted`. When
--    workers_accepted == workers_needed, the job automatically
--    closes for new applications.
--    This is enforced in the Java service layer WITHIN A TRANSACTION:
--      a) Lock the job row (SELECT ... FOR UPDATE)
--      b) Check workers_accepted < workers_needed
--      c) Increment workers_accepted
--      d) Update application status to ACCEPTED
--    This prevents the classic race condition where 6 workers get
--    accepted for a 5-worker job because two acceptance requests
--    were processed simultaneously.
--
-- 2. STATE MACHINE FOR APPLICATIONS:
--    Valid transitions (enforced in Java, not SQL):
--      APPLIED     → SHORTLISTED | REJECTED | WITHDRAWN
--      SHORTLISTED → ACCEPTED | REJECTED | WITHDRAWN
--      ACCEPTED    → COMPLETED | WITHDRAWN
--    INVALID transitions (blocked in Java):
--      REJECTED    → ACCEPTED (cannot re-accept a rejected applicant)
--      WITHDRAWN   → APPLIED (cannot re-apply via status change)
--      COMPLETED   → any (terminal state)
--
-- 3. UNIQUE(job_id, applicant_id):
--    A person can only apply to the same job ONCE. This prevents
--    spam applications and simplifies the employer's review process.
--
-- 4. EMPLOYER RATING OF WORKERS:
--    After marking an application as COMPLETED, the employer can rate
--    the worker. This is stored directly on the job_applications row
--    (not in a separate reviews table) because job worker ratings
--    have a different structure than service provider reviews.
-- ============================================================================

-- ============================================================================
-- JOB CATEGORIES
-- ============================================================================
CREATE TABLE job_categories (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(50)  NOT NULL,
    display_name    VARCHAR(100) NOT NULL,
    description     TEXT,
    icon            VARCHAR(50),
    is_active       BOOLEAN     NOT NULL DEFAULT TRUE,
    display_order   INT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_job_categories_name UNIQUE (name)
);

COMMENT ON TABLE job_categories IS 'Types of local jobs — Harvesting, Sowing, Construction, Transport, etc.';

-- ============================================================================
-- JOBS
-- ============================================================================
CREATE TABLE jobs (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    employer_id         UUID        NOT NULL,
    village_id          UUID        NOT NULL,
    category_id         UUID        NOT NULL,

    -- Job details
    title               VARCHAR(150) NOT NULL,
    description         TEXT,
    workers_needed      INT         NOT NULL,
    workers_accepted    INT         NOT NULL DEFAULT 0,
    daily_wage          DECIMAL(10,2) NOT NULL,
    total_budget        DECIMAL(12,2),

    -- Schedule
    start_date          DATE        NOT NULL,
    end_date            DATE        NOT NULL,

    -- Requirements
    required_skills     TEXT,
    min_experience_years INT       DEFAULT 0,
    gender_preference   VARCHAR(10),

    -- Location
    location_details    TEXT,
    latitude            DECIMAL(9,6),
    longitude           DECIMAL(9,6),

    -- Status
    status              VARCHAR(30) NOT NULL DEFAULT 'OPEN',

    -- Timestamps
    filled_at           TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_jobs_employer       FOREIGN KEY (employer_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_jobs_village        FOREIGN KEY (village_id) REFERENCES villages(id) ON DELETE RESTRICT,
    CONSTRAINT fk_jobs_category       FOREIGN KEY (category_id) REFERENCES job_categories(id) ON DELETE RESTRICT,
    CONSTRAINT chk_jobs_workers       CHECK (workers_needed > 0),
    CONSTRAINT chk_jobs_accepted      CHECK (workers_accepted >= 0 AND workers_accepted <= workers_needed),
    CONSTRAINT chk_jobs_dates         CHECK (end_date >= start_date),
    CONSTRAINT chk_jobs_wage          CHECK (daily_wage > 0),
    CONSTRAINT chk_jobs_status        CHECK (status IN (
        'OPEN', 'FILLED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'EXPIRED'
    )),
    CONSTRAINT chk_jobs_gender        CHECK (gender_preference IS NULL OR gender_preference IN ('MALE', 'FEMALE', 'ANY'))
);

COMMENT ON TABLE jobs IS 'Temporary/seasonal job postings by farmers and local employers';
COMMENT ON COLUMN jobs.workers_needed IS 'Maximum capacity — applications close when workers_accepted reaches this';
COMMENT ON COLUMN jobs.workers_accepted IS 'Denormalized counter — updated transactionally to prevent race conditions';
COMMENT ON COLUMN jobs.status IS 'Auto-transitions to FILLED when workers_accepted == workers_needed';

-- ============================================================================
-- JOB APPLICATIONS
-- ============================================================================
CREATE TABLE job_applications (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id              UUID        NOT NULL,
    applicant_id        UUID        NOT NULL,

    -- Application details
    status              VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    cover_note          TEXT,

    -- Employer review of worker (after completion)
    employer_rating     INT,
    employer_feedback   TEXT,

    -- Worker review of employer (after completion)
    worker_rating       INT,
    worker_feedback     TEXT,

    -- Timestamps
    shortlisted_at      TIMESTAMP WITH TIME ZONE,
    accepted_at         TIMESTAMP WITH TIME ZONE,
    rejected_at         TIMESTAMP WITH TIME ZONE,
    withdrawn_at        TIMESTAMP WITH TIME ZONE,
    completed_at        TIMESTAMP WITH TIME ZONE,
    applied_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_applications_job       FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    CONSTRAINT fk_applications_applicant FOREIGN KEY (applicant_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uk_applications_job_user  UNIQUE (job_id, applicant_id),
    CONSTRAINT chk_applications_status   CHECK (status IN (
        'APPLIED', 'SHORTLISTED', 'ACCEPTED', 'REJECTED', 'WITHDRAWN', 'COMPLETED'
    )),
    CONSTRAINT chk_applications_emp_rating CHECK (employer_rating IS NULL OR employer_rating BETWEEN 1 AND 5),
    CONSTRAINT chk_applications_wrk_rating CHECK (worker_rating IS NULL OR worker_rating BETWEEN 1 AND 5)
);

COMMENT ON TABLE job_applications IS 'Worker applications to jobs — one application per person per job';
COMMENT ON COLUMN job_applications.status IS 'State machine transitions enforced in Java service layer';

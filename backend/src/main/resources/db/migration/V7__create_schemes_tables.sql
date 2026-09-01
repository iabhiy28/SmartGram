-- ============================================================================
-- V7: Government Scheme Discovery Tables
-- ============================================================================
-- This module helps villagers discover government welfare schemes they
-- might be eligible for. India runs hundreds of central and state schemes
-- (PM-KISAN, MGNREGA, Ayushman Bharat, etc.) but most rural citizens
-- don't know which ones apply to them.
--
-- IMPORTANT DISCLAIMER (built into the application):
-- "This is an informational eligibility screening tool. It matches your
--  profile against published scheme criteria. Final eligibility is
--  determined by the relevant government authority."
--
-- KEY DESIGN DECISIONS:
--
-- 1. RULE-BASED ELIGIBILITY SCREENING (not hardcoded):
--    Instead of writing Java code like:
--      if (user.age >= 18 && user.age <= 40 && user.income < 250000) ...
--
--    We store eligibility rules as DATA in the scheme_eligibility_rules
--    table. This means:
--    a) New schemes can be added by Super Admin without code changes
--    b) Rules can be updated when the government changes criteria
--    c) The screening engine is generic and reusable
--
-- 2. HOW THE SCREENING ENGINE WORKS:
--    For each scheme, load its eligibility rules.
--    For each rule, check if the user's profile matches:
--      - AGE rule: user's age must be between min_value and max_value
--      - INCOME rule: user's annual income must be <= max_value
--      - GENDER rule: user's gender must match exact_value (or NULL = any)
--      - CASTE rule: user's caste_category must match exact_value
--      - LAND rule: user's land_ownership must match (true/false)
--      - OCCUPATION rule: user's occupation must be in target list
--    A scheme is "potentially eligible" if ALL its rules match.
--
-- 3. required_documents AS JSONB:
--    Documents vary wildly between schemes. Rather than creating a
--    separate normalized table for 3-4 document names per scheme,
--    we store them as a JSON array. This is one of the valid use cases
--    for JSONB in PostgreSQL — small, rarely-queried metadata that
--    doesn't need its own table/indexes.
--
-- 4. STATE_ID NULLABLE for central (all-India) schemes:
--    If state_id IS NULL, the scheme is a central government scheme
--    available across all states. If state_id is set, it's a state-
--    specific scheme.
-- ============================================================================

-- ============================================================================
-- GOVERNMENT SCHEMES
-- ============================================================================
CREATE TABLE government_schemes (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    title               VARCHAR(200) NOT NULL,
    title_hindi         VARCHAR(200),
    title_kannada       VARCHAR(200),
    department          VARCHAR(150),
    state_id            UUID,
    category            VARCHAR(50),

    -- Content
    description         TEXT        NOT NULL,
    benefits_summary    TEXT,
    required_documents  JSONB       NOT NULL DEFAULT '[]'::jsonb,
    application_process TEXT,
    application_link    VARCHAR(500),
    helpline_number     VARCHAR(20),

    -- Status & Verification
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    last_verified_at    DATE,
    source_url          VARCHAR(500),

    -- Timestamps
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_schemes_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE RESTRICT
);

COMMENT ON TABLE government_schemes IS 'Government welfare scheme catalog — central (state_id=NULL) and state-specific';
COMMENT ON COLUMN government_schemes.required_documents IS 'JSON array of document names, e.g., ["Aadhaar Card", "Income Certificate", "Land Record"]';
COMMENT ON COLUMN government_schemes.source_url IS 'Official government source URL for verification — informational integrity';

-- ============================================================================
-- SCHEME ELIGIBILITY RULES
-- ============================================================================
-- Each scheme has one or more eligibility criteria stored as rules.
-- The screening engine evaluates ALL rules for a scheme against the
-- user's profile. If ALL rules pass, the scheme is shown as "potentially
-- eligible".
--
-- RULE TYPES:
--   AGE         → check user age between min_value and max_value
--   INCOME      → check user annual_income <= max_value
--   GENDER      → check user gender == exact_value (NULL means any)
--   CASTE       → check user caste_category == exact_value
--   LAND        → check user land_ownership == (exact_value == 'true')
--   OCCUPATION  → check user occupation IN (target_occupations array)
--   STATE       → check user's state matches (handled by the FK above)
-- ============================================================================
CREATE TABLE scheme_eligibility_rules (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    scheme_id           UUID        NOT NULL,
    rule_type           VARCHAR(50) NOT NULL,
    min_value           VARCHAR(100),
    max_value           VARCHAR(100),
    exact_value         VARCHAR(100),
    target_occupations  JSONB,
    description         VARCHAR(200),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rules_scheme FOREIGN KEY (scheme_id) REFERENCES government_schemes(id) ON DELETE CASCADE,
    CONSTRAINT chk_rules_type  CHECK (rule_type IN (
        'AGE', 'INCOME', 'GENDER', 'CASTE', 'LAND', 'OCCUPATION', 'STATE'
    ))
);

COMMENT ON TABLE scheme_eligibility_rules IS 'Data-driven eligibility criteria — evaluated by the screening engine at runtime';
COMMENT ON COLUMN scheme_eligibility_rules.rule_type IS 'Type of criteria check — determines which user field to evaluate';
COMMENT ON COLUMN scheme_eligibility_rules.target_occupations IS 'JSON array for OCCUPATION rules, e.g., ["FARMER", "AGRICULTURAL_LABORER"]';

-- ============================================================================
-- USER SAVED SCHEMES (Bookmarks)
-- ============================================================================
-- Users can save schemes they want to review later or apply for.
-- ============================================================================
CREATE TABLE user_saved_schemes (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    scheme_id   UUID        NOT NULL,
    saved_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_saved_schemes_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_schemes_scheme FOREIGN KEY (scheme_id) REFERENCES government_schemes(id) ON DELETE CASCADE,
    CONSTRAINT uk_saved_schemes        UNIQUE (user_id, scheme_id)
);

COMMENT ON TABLE user_saved_schemes IS 'User bookmarks for government schemes — prevents duplicate saves';

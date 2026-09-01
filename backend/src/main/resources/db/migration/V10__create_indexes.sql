-- ============================================================================
-- V10: Performance, Foreign Key & Search Indexes
-- ============================================================================
-- Indexes are critical for keeping P95 response times under 150ms as the
-- dataset scales from a single village to tens of thousands of villages.
--
-- INDEXING STRATEGY:
--
-- 1. FOREIGN KEY INDEXES:
--    PostgreSQL does NOT automatically index foreign key columns. Without FK
--    indexes, JOINs and cascading checks result in sequential table scans.
--
-- 2. COMPOSITE INDEXES FOR FREQUENT QUERY PATTERNS:
--    - Directory Search: (village_id, category_id, is_active, average_rating)
--    - SLA Breach Detection: (status, expected_resolution_at, is_overdue)
--    - Overlapping Equipment Booking Check: (equipment_id, status, start_date, end_date)
--    - Active Jobs by Village: (village_id, status, start_date)
--    - User Unread Notifications: (recipient_id, is_read, created_at DESC)
--
-- 3. GEOSPATIAL BOUNDING BOX INDEXES:
--    - Composite B-Tree index on (latitude, longitude) for fast bounding box
--      coordinate filtering.
--
-- 4. PARTIAL INDEXES:
--    - Indexes with WHERE clauses to minimize index footprint (e.g., only active items).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Hierarchy & Location Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_districts_state_id ON districts(state_id);
CREATE INDEX idx_panchayats_district_id ON panchayats(district_id);
CREATE INDEX idx_villages_panchayat_id ON villages(panchayat_id);
CREATE INDEX idx_villages_pin_code ON villages(pin_code);
CREATE INDEX idx_villages_coords ON villages(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- ----------------------------------------------------------------------------
-- 2. Users & Authentication Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_users_village_id ON users(village_id);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_village_role ON users(village_id, role, is_active);
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_active ON refresh_tokens(user_id, is_revoked, expires_at) WHERE is_revoked = FALSE;

-- ----------------------------------------------------------------------------
-- 3. Services Marketplace Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_providers_user_id ON service_provider_profiles(user_id);
CREATE INDEX idx_providers_status_avail ON service_provider_profiles(verification_status, is_available) WHERE verification_status = 'VERIFIED';
CREATE INDEX idx_providers_rating ON service_provider_profiles(average_rating DESC);

CREATE INDEX idx_offerings_provider_id ON service_offerings(provider_id);
CREATE INDEX idx_offerings_category_id ON service_offerings(category_id);
CREATE INDEX idx_offerings_active_cat ON service_offerings(category_id, is_active) WHERE is_active = TRUE;

CREATE INDEX idx_service_bookings_villager ON service_bookings(villager_id, created_at DESC);
CREATE INDEX idx_service_bookings_provider ON service_bookings(provider_id, status, scheduled_date);
CREATE INDEX idx_service_bookings_offering ON service_bookings(offering_id);

-- ----------------------------------------------------------------------------
-- 4. Equipment Rental & Double-Booking Query Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_equipment_owner ON equipment(owner_id);
CREATE INDEX idx_equipment_village_cat ON equipment(village_id, category_id, is_operational, is_active);
CREATE INDEX idx_equipment_coords ON equipment(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- Concurrency check index: Used by SELECT ... FOR UPDATE query during booking check
CREATE INDEX idx_equip_booking_concurrency ON equipment_bookings(equipment_id, status, start_date, end_date);
CREATE INDEX idx_equip_booking_renter ON equipment_bookings(renter_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- 5. Jobs & Application Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_jobs_employer ON jobs(employer_id);
CREATE INDEX idx_jobs_village_status ON jobs(village_id, status, category_id, created_at DESC);
CREATE INDEX idx_jobs_active_feed ON jobs(status, start_date) WHERE status = 'OPEN';

CREATE INDEX idx_job_apps_job_id ON job_applications(job_id, status);
CREATE INDEX idx_job_apps_applicant ON job_applications(applicant_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- 6. Complaints & SLA Engine Performance Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_complaints_citizen ON complaints(citizen_id, created_at DESC);
CREATE INDEX idx_complaints_village_status ON complaints(village_id, status, priority, created_at DESC);
CREATE INDEX idx_complaints_assigned_to ON complaints(assigned_to_id, status) WHERE assigned_to_id IS NOT NULL;

-- SLA Cron Query optimization index (scans only non-resolved complaints with expected dates)
CREATE INDEX idx_complaints_sla_overdue_scan ON complaints(status, expected_resolution_at, is_overdue) 
WHERE status NOT IN ('RESOLVED', 'CLOSED') AND expected_resolution_at IS NOT NULL;

CREATE INDEX idx_complaints_duplicate ON complaints(duplicate_of_id) WHERE duplicate_of_id IS NOT NULL;
CREATE INDEX idx_complaint_attachments_complaint ON complaint_attachments(complaint_id);
CREATE INDEX idx_complaint_comments_complaint ON complaint_comments(complaint_id, created_at ASC);

-- ----------------------------------------------------------------------------
-- 7. Schemes & Eligibility Rules Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_schemes_state_active ON government_schemes(state_id, is_active);
CREATE INDEX idx_schemes_category ON government_schemes(category, is_active);
CREATE INDEX idx_scheme_rules_scheme_id ON scheme_eligibility_rules(scheme_id, rule_type);
CREATE INDEX idx_user_saved_schemes_user ON user_saved_schemes(user_id);

-- ----------------------------------------------------------------------------
-- 8. Announcements & Emergency Contacts Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_announcements_panchayat ON announcements(panchayat_id, priority, created_at DESC);
CREATE INDEX idx_announcements_village ON announcements(village_id, is_active) WHERE village_id IS NOT NULL;
CREATE INDEX idx_announcements_active_feed ON announcements(panchayat_id, is_active, expires_at);

CREATE INDEX idx_emergency_panchayat_cat ON emergency_contacts(panchayat_id, category, is_active, display_order ASC);

-- ----------------------------------------------------------------------------
-- 9. Reviews, Notifications & Audit Log Indexes
-- ----------------------------------------------------------------------------
CREATE INDEX idx_reviews_provider ON reviews(provider_id, created_at DESC);
CREATE INDEX idx_reviews_reviewer ON reviews(reviewer_id);

-- Notification center feed index (fetches user's unread notifications fast)
CREATE INDEX idx_notifications_recipient_unread ON notifications(recipient_id, is_read, created_at DESC);

CREATE INDEX idx_saved_services_user ON user_saved_services(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_name, entity_id, created_at DESC);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id, created_at DESC);

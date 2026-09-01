-- ============================================================================
-- V11: Master Seed & Reference Data
-- ============================================================================
-- Seed master reference data including administrative hierarchy, categories,
-- complaint SLAs, and baseline government welfare schemes with eligibility rules.
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. States & Union Territories
-- ----------------------------------------------------------------------------
INSERT INTO states (id, name, code) VALUES
('a0000000-0000-0000-0000-000000000001', 'Karnataka', 'KA'),
('a0000000-0000-0000-0000-000000000002', 'Maharashtra', 'MH'),
('a0000000-0000-0000-0000-000000000003', 'Uttar Pradesh', 'UP'),
('a0000000-0000-0000-0000-000000000004', 'Tamil Nadu', 'TN'),
('a0000000-0000-0000-0000-000000000005', 'Bihar', 'BR');

-- ----------------------------------------------------------------------------
-- 2. Sample Districts (Karnataka & Maharashtra)
-- ----------------------------------------------------------------------------
INSERT INTO districts (id, state_id, name, code) VALUES
('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 'Ramanagara', 'RAM'),
('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001', 'Mandya', 'MAN'),
('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000002', 'Pune', 'PUN');

-- ----------------------------------------------------------------------------
-- 3. Sample Panchayats
-- ----------------------------------------------------------------------------
INSERT INTO panchayats (id, district_id, name, office_address, contact_phone) VALUES
('c0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000001', 'Bidadi Gram Panchayat', 'Main Road, Bidadi, Ramanagara - 562109', '+918027282001'),
('c0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'Harohalli Gram Panchayat', 'Kanakapura Road, Harohalli - 562112', '+918027282002'),
('c0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000002', 'Maddur Gram Panchayat', 'Station Road, Maddur, Mandya - 571428', '+918232282001');

-- ----------------------------------------------------------------------------
-- 4. Sample Villages
-- ----------------------------------------------------------------------------
INSERT INTO villages (id, panchayat_id, name, pin_code, latitude, longitude, population) VALUES
('d0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'Kempadyapanahalli', '562109', 12.795600, 77.382400, 2450),
('d0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001', 'Byramangala', '562109', 12.771200, 77.418900, 3800),
('d0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000002', 'Maralebekuppe', '562112', 12.658900, 77.452300, 1920),
('d0000000-0000-0000-0000-000000000004', 'c0000000-0000-0000-0000-000000000003', 'Shivapura', '571428', 12.584100, 77.042100, 4100);

-- ----------------------------------------------------------------------------
-- 5. Master Service Categories
-- ----------------------------------------------------------------------------
INSERT INTO service_categories (id, name, display_name, description, icon, display_order) VALUES
(gen_random_uuid(), 'ELECTRICIAN', 'Electrician', 'Wiring, fuse repair, motor starter fixes, pump repairs', 'Zap', 1),
(gen_random_uuid(), 'PLUMBER', 'Plumber', 'Pipe leaks, tap fixing, borewell connections, drainage', 'Wrench', 2),
(gen_random_uuid(), 'CARPENTER', 'Carpenter', 'Door/window fixes, wooden farm tools, furniture', 'Hammer', 3),
(gen_random_uuid(), 'MECHANIC', 'Mechanic & Vehicle Repair', 'Tractor, two-wheeler, auto, and motor servicing', 'Settings', 4),
(gen_random_uuid(), 'MASON', 'Mason / Builder', 'Brickwork, plastering, floor repair, small construction', 'HardHat', 5),
(gen_random_uuid(), 'PAINTER', 'Painter', 'House whitewash, painting, waterproof coating', 'Paintbrush', 6),
(gen_random_uuid(), 'DRIVER', 'Driver', 'Tractor, commercial pickup, car, transport driver', 'Car', 7),
(gen_random_uuid(), 'WELDER', 'Welder & Fabrication', 'Gate repair, agricultural implement welding, grills', 'Flame', 8),
(gen_random_uuid(), 'MOBILE_REPAIR', 'Mobile & Electronics', 'Phone repair, battery replacement, screen fix', 'Smartphone', 9),
(gen_random_uuid(), 'CABLE_INTERNET', 'Internet & DTH / Cable', 'DTH setup, cable connection, Wi-Fi router setup', 'Tv', 10),
(gen_random_uuid(), 'TAILOR', 'Tailor', 'Stitching, alterations, school uniforms, garments', 'Scissors', 11),
(gen_random_uuid(), 'GROCERY_SUPPLY', 'Grocery & Essentials', 'Local provision store and staple delivery', 'ShoppingBag', 12),
(gen_random_uuid(), 'MEDICAL_STORE', 'Pharmacy & Medical', 'Medicines, first aid, veterinary supplies', 'PlusCircle', 13),
(gen_random_uuid(), 'OTHER', 'Other Services', 'Other specialized village services', 'MoreHorizontal', 99);

-- ----------------------------------------------------------------------------
-- 6. Master Equipment Categories
-- ----------------------------------------------------------------------------
INSERT INTO equipment_categories (id, name, display_name, description, icon, display_order) VALUES
(gen_random_uuid(), 'TRACTOR', 'Tractor', '35HP to 60HP Tractors for tilling, ploughing, and haulage', 'Truck', 1),
(gen_random_uuid(), 'ROTAVATOR', 'Rotavator', 'Soil preparation and rotary tilling equipment', 'Layers', 2),
(gen_random_uuid(), 'HARVESTER', 'Combine Harvester', 'Crop harvesting, threshing, and cleaning machine', 'Wheat', 3),
(gen_random_uuid(), 'WATER_PUMP', 'Diesel / Electric Water Pump', 'Portable water pump sets for irrigation and de-watering', 'Droplets', 4),
(gen_random_uuid(), 'SPRAYER', 'Power Sprayer', 'Battery and fuel power sprayers for pesticide/fertilizer', 'Shield', 5),
(gen_random_uuid(), 'CULTIVATOR', 'Cultivator & Plough', '9-tyne cultivators, disc ploughs, and harrows', 'Tool', 6),
(gen_random_uuid(), 'THRESHER', 'Multi-Crop Thresher', 'Grain thresher for paddy, ragi, wheat, and maize', 'Sun', 7),
(gen_random_uuid(), 'TRAILER', 'Tractor Trolley / Trailer', 'Tipping and non-tipping trailers for produce transport', 'Package', 8);

-- ----------------------------------------------------------------------------
-- 7. Master Job Categories
-- ----------------------------------------------------------------------------
INSERT INTO job_categories (id, name, display_name, description, icon, display_order) VALUES
(gen_random_uuid(), 'HARVESTING', 'Crop Harvesting', 'Paddy, sugarcane, vegetable, and grain harvesting labor', 'Wheat', 1),
(gen_random_uuid(), 'SOWING_TRANSPLANTING', 'Sowing & Transplanting', 'Seed sowing, paddy transplanting, nursery preparation', 'Sprout', 2),
(gen_random_uuid(), 'WEEDING_MAINTENANCE', 'Weeding & Field Maintenance', 'Manual weeding, bunding, canal clearing, land leveling', 'Scissors', 3),
(gen_random_uuid(), 'CONSTRUCTION', 'Construction Labor', 'Masonry helper, concrete mixing, excavation, building work', 'HardHat', 4),
(gen_random_uuid(), 'LOADING_TRANSPORT', 'Loading & Produce Transport', 'Bag loading, unloading at Mandi, grain transport helper', 'Truck', 5),
(gen_random_uuid(), 'LIVESTOCK_DAIRY', 'Dairy & Livestock Care', 'Cattle milking, shed cleaning, fodder cutting', 'Heart', 6);

-- ----------------------------------------------------------------------------
-- 8. Master Complaint Categories with SLAs & Assigned Departments
-- ----------------------------------------------------------------------------
INSERT INTO complaint_categories (id, name, display_name, description, default_sla_hours, default_priority, responsible_department, icon, display_order) VALUES
(gen_random_uuid(), 'ELECTRICITY_EMERGENCY', 'Electricity Fault / Live Wire', 'Power outage, fallen pole, sparking transformer, live wire danger', 4, 'CRITICAL', 'Electricity Board (BESCOM / State Discom)', 'Zap', 1),
(gen_random_uuid(), 'WATER_SUPPLY', 'Drinking Water & Hand Pump', 'Pipeline leakage, pump failure, dirty water supply, valve break', 12, 'HIGH', 'Rural Water Supply & Sanitation (RWSS)', 'Droplets', 2),
(gen_random_uuid(), 'SANITATION_DRAINAGE', 'Drainage & Garbage Overflow', 'Choked open drain, stagnant sewage water, garbage accumulation', 24, 'MEDIUM', 'Gram Panchayat Health & Sanitation Dept', 'Trash2', 3),
(gen_random_uuid(), 'STREETLIGHT', 'Broken Streetlight', 'Streetlight not working, damaged bulb, broken timer switch', 48, 'LOW', 'Panchayat Electrical Maintenance', 'Lightbulb', 4),
(gen_random_uuid(), 'ROAD_INFRASTRUCTURE', 'Damaged Road & Potholes', 'Cracked road, culvert damage, deep potholes, unpaved stretch', 168, 'MEDIUM', 'Public Works Department (PWD / Panchayat Roads)', 'AlertTriangle', 5),
(gen_random_uuid(), 'PUBLIC_TOILET', 'Community Toilet Facility', 'No water in public toilet, damaged doors, sanitation issue', 24, 'MEDIUM', 'Swachh Bharat Gramin Cell', 'Home', 6),
(gen_random_uuid(), 'PUBLIC_HEALTH_VET', 'Public Health & PHC Service', 'Doctor absence, mosquito fogging requirement, stray animal hazard', 8, 'HIGH', 'Primary Health Centre (PHC) Officer', 'Activity', 7),
(gen_random_uuid(), 'OTHER', 'Other Village Issue', 'School infrastructure, community hall maintenance, general grievance', 72, 'LOW', 'Gram Panchayat General Administration', 'HelpCircle', 99);

-- ----------------------------------------------------------------------------
-- 9. Government Welfare Schemes & Eligibility Rules
-- ----------------------------------------------------------------------------

-- Scheme 1: PM-KISAN (Central)
INSERT INTO government_schemes (id, title, title_hindi, title_kannada, department, state_id, category, description, benefits_summary, required_documents, application_process, application_link, helpline_number, source_url) VALUES
('e0000000-0000-0000-0000-000000000001',
 'Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)',
 'प्रधानमंत्री किसान सम्मान निधि',
 'ಪ್ರಧಾನ ಮಂತ್ರಿ ಕಿಸಾನ್ ಸಮ್ಮಾನ್ ನಿಧಿ',
 'Ministry of Agriculture and Farmers Welfare',
 NULL, -- Central Scheme (all states)
 'AGRICULTURE',
 'Direct income support of ₹6,000 per year to all landholding farmer families across India in three equal 4-monthly installments.',
 '₹6,000 per annum paid directly to bank account via DBT (₹2,000 every 4 months).',
 '["Aadhaar Card", "Land Ownership Record (RTC/Pahani)", "Bank Account Passbook (Aadhaar linked)", "Mobile Number"]'::jsonb,
 'Apply online on pmkisan.gov.in or visit the nearest Common Service Centre (CSC) / Gram Panchayat office.',
 'https://pmkisan.gov.in',
 '155261',
 'https://pmkisan.gov.in/About_Us.aspx');

INSERT INTO scheme_eligibility_rules (scheme_id, rule_type, min_value, max_value, exact_value, target_occupations, description) VALUES
('e0000000-0000-0000-0000-000000000001', 'LAND', NULL, NULL, 'true', NULL, 'Must possess cultivable landholding in own/family name'),
('e0000000-0000-0000-0000-000000000001', 'AGE', '18', '80', NULL, NULL, 'Age between 18 and 80 years'),
('e0000000-0000-0000-0000-000000000001', 'OCCUPATION', NULL, NULL, NULL, '["FARMER", "AGRICULTURAL_WORKER"]'::jsonb, 'Must be an active farmer or land cultivator');

-- Scheme 2: Ayushman Bharat - PM-JAY (Central)
INSERT INTO government_schemes (id, title, title_hindi, title_kannada, department, state_id, category, description, benefits_summary, required_documents, application_process, application_link, helpline_number, source_url) VALUES
('e0000000-0000-0000-0000-000000000002',
 'Ayushman Bharat - PM Jan Arogya Yojana (PM-JAY)',
 'आयुष्मान भारत - प्रधानमंत्री जन आरोग्य योजना',
 'ಆಯುಷ್ಮಾನ್ ಭಾರತ - ಪ್ರಧಾನ ಮಂತ್ರಿ ಜನ ಆರೋಗ್ಯ ಯೋಜನೆ',
 'National Health Authority',
 NULL,
 'HEALTHCARE',
 'World largest health insurance scheme providing ₹5,00,000 cashless secondary and tertiary hospitalization cover per family per year.',
 'Cashless treatment up to ₹5 Lakh/year across all empaneled public and private hospitals across India.',
 '["Aadhaar Card", "Ration Card (BPL/Antyodaya)", "Income Certificate"]'::jsonb,
 'Visit nearest Primary Health Centre, Ayushman Arogya Mandir, or empaneled hospital with Aadhaar and Ration card.',
 'https://pmjay.gov.in',
 '14555',
 'https://pmjay.gov.in/about/pmjay');

INSERT INTO scheme_eligibility_rules (scheme_id, rule_type, min_value, max_value, exact_value, target_occupations, description) VALUES
('e0000000-0000-0000-0000-000000000002', 'INCOME', NULL, '250000', NULL, NULL, 'Annual household income under ₹2.5 Lakh (or BPL Ration Card holder)'),
('e0000000-0000-0000-0000-000000000002', 'AGE', '0', '100', NULL, NULL, 'All age groups covered');

-- Scheme 3: Karnataka Ganga Kalyana Scheme (State: Karnataka)
INSERT INTO government_schemes (id, title, title_hindi, title_kannada, department, state_id, category, description, benefits_summary, required_documents, application_process, application_link, helpline_number, source_url) VALUES
('e0000000-0000-0000-0000-000000000003',
 'Karnataka Ganga Kalyana Borewell Scheme',
 'कर्नाटक गंगा कल्याण योजना',
 'ಕರ್ನಾಟಕ ಗಂಗಾ ಕಲ್ಯಾಣ ಯೋಜನೆ',
 'Social Welfare & Backward Classes Dept, Govt of Karnataka',
 'a0000000-0000-0000-0000-000000000001', -- Karnataka
 'IRRIGATION',
 'Provides subsidized borewells, submersible pump sets, and electrification for small and marginal farmers belonging to SC/ST and OBC categories in Karnataka.',
 '100% subsidized individual/community borewell drilling and pump energization (up to ₹3.5 Lakh value).',
 '["Aadhaar Card", "Caste Certificate (SC/ST/OBC)", "Income Certificate (< ₹2 Lakh)", "RTC / Pahani Land Record (1 to 5 acres)"]'::jsonb,
 'Apply online on Seva Sindhu / KMDAC portal or through Karnataka One / Grama One centers.',
 'https://sevasindhu.karnataka.gov.in',
 '1902',
 'https://kmdac.karnataka.gov.in');

INSERT INTO scheme_eligibility_rules (scheme_id, rule_type, min_value, max_value, exact_value, target_occupations, description) VALUES
('e0000000-0000-0000-0000-000000000003', 'STATE', NULL, NULL, 'KA', NULL, 'Must be a permanent resident of Karnataka'),
('e0000000-0000-0000-0000-000000000003', 'LAND', NULL, NULL, 'true', NULL, 'Must own 1 to 5 acres of agricultural land'),
('e0000000-0000-0000-0000-000000000003', 'INCOME', NULL, '200000', NULL, NULL, 'Annual income under ₹2,00,000'),
('e0000000-0000-0000-0000-000000000003', 'CASTE', NULL, NULL, 'OBC', NULL, 'Targeted for SC, ST, and OBC backward community farmers');

-- ----------------------------------------------------------------------------
-- 10. Sample Verified Emergency Directory (Bidadi Panchayat)
-- ----------------------------------------------------------------------------
INSERT INTO emergency_contacts (id, panchayat_id, service_name, category, contact_person, phone_number, alternate_phone, address, operating_hours, is_verified, display_order) VALUES
(gen_random_uuid(), 'c0000000-0000-0000-0000-000000000001', 'Government General Hospital / PHC Bidadi', 'PHC', 'Dr. Ramesh Kumar (Medical Officer)', '+918027282110', '+919448012345', 'Near Bus Stand, Bidadi Main Road', '24 Hours Emergency', TRUE, 1),
(gen_random_uuid(), 'c0000000-0000-0000-0000-000000000001', 'Emergency Ambulance Service', 'AMBULANCE', 'State Central Emergency Desk', '108', '112', 'Dispatched from Bidadi Community Health Centre', '24 Hours / 7 Days', TRUE, 2),
(gen_random_uuid(), 'c0000000-0000-0000-0000-000000000001', 'Bidadi Police Station', 'POLICE', 'Inspector Ananda Gowda', '+918027282222', '112', 'Bangalore-Mysore Highway, Bidadi', '24 Hours', TRUE, 3),
(gen_random_uuid(), 'c0000000-0000-0000-0000-000000000001', 'BESCOM Rural Electricity Helpline', 'ELECTRICITY', 'Junior Engineer (Rural Grid)', '+918027282333', '1912', 'BESCOM Substation, Harohalli Road', '6:00 AM - 10:00 PM', TRUE, 4),
(gen_random_uuid(), 'c0000000-0000-0000-0000-000000000001', 'Gram Panchayat Drinking Water Maintenance', 'WATER', 'Manjunath (Valve & Pump Inspector)', '+919845098765', NULL, 'Gram Panchayat Office, Bidadi', '7:00 AM - 7:00 PM', TRUE, 5),
(gen_random_uuid(), 'c0000000-0000-0000-0000-000000000001', 'Bidadi Fire & Rescue Station', 'FIRE', 'Station Officer', '+918027282444', '101', 'KIADB Industrial Area, Phase 1', '24 Hours', TRUE, 6),
(gen_random_uuid(), 'c0000000-0000-0000-0000-000000000001', 'Government Veterinary Hospital', 'VETERINARY', 'Dr. Shwetha (Veterinary Surgeon)', '+918027282555', NULL, 'Opposite APMC Yard, Bidadi', '8:00 AM - 2:00 PM', TRUE, 7);

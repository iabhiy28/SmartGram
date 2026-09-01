package com.gramconnect.common.config;

import com.gramconnect.modules.complaint.entity.ComplaintCategory;
import com.gramconnect.modules.complaint.repository.ComplaintCategoryRepository;
import com.gramconnect.modules.equipment.entity.EquipmentCategory;
import com.gramconnect.modules.equipment.repository.EquipmentCategoryRepository;
import com.gramconnect.modules.hierarchy.entity.*;
import com.gramconnect.modules.hierarchy.repository.*;
import com.gramconnect.modules.job.entity.JobCategory;
import com.gramconnect.modules.job.repository.JobCategoryRepository;
import com.gramconnect.modules.scheme.entity.GovernmentScheme;
import com.gramconnect.modules.scheme.repository.GovernmentSchemeRepository;
import com.gramconnect.modules.service.entity.ServiceCategory;
import com.gramconnect.modules.service.repository.ServiceCategoryRepository;
import com.gramconnect.modules.user.entity.Role;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Seeds initial demo data if database is empty on startup.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final PanchayatRepository panchayatRepository;
    private final VillageRepository villageRepository;
    private final UserRepository userRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final EquipmentCategoryRepository equipmentCategoryRepository;
    private final ComplaintCategoryRepository complaintCategoryRepository;
    private final GovernmentSchemeRepository schemeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (stateRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data population.");
            return;
        }

        log.info("Populating initial demo reference data...");

        // 1. Hierarchy
        State karnataka = State.builder().name("Karnataka").code("KA").build();
        stateRepository.save(karnataka);

        District ramanagara = District.builder().state(karnataka).name("Ramanagara").build();
        districtRepository.save(ramanagara);

        Panchayat bidadi = Panchayat.builder().district(ramanagara).name("Bidadi Gram Panchayat").build();
        panchayatRepository.save(bidadi);

        Village bidadiVillage = Village.builder()
                .panchayat(bidadi)
                .name("Bidadi Village")
                .pinCode("562109")
                .population(4500)
                .latitude(BigDecimal.valueOf(12.798100))
                .longitude(BigDecimal.valueOf(77.382600))
                .build();
        villageRepository.save(bidadiVillage);

        // 2. Admin User
        User admin = User.builder()
                .phoneNumber("9999999999")
                .passwordHash(passwordEncoder.encode("Admin@123"))
                .fullName("Panchayat Admin Ramesh")
                .role(Role.ROLE_PANCHAYAT_ADMIN)
                .villageId(bidadiVillage.getId())
                .isActive(true)
                .isVerified(true)
                .build();
        userRepository.save(admin);

        // Demo Villager
        User villager = User.builder()
                .phoneNumber("9876543210")
                .passwordHash(passwordEncoder.encode("User@123"))
                .fullName("Gopal Gowda")
                .role(Role.ROLE_VILLAGER)
                .villageId(bidadiVillage.getId())
                .isActive(true)
                .isVerified(true)
                .build();
        userRepository.save(villager);

        // 3. Service Categories
        serviceCategoryRepository.save(ServiceCategory.builder().name("ELECTRICIAN").displayName("Electrician").icon("Zap").displayOrder(1).build());
        serviceCategoryRepository.save(ServiceCategory.builder().name("PLUMBER").displayName("Plumber").icon("Droplets").displayOrder(2).build());
        serviceCategoryRepository.save(ServiceCategory.builder().name("MASON").displayName("Mason / Mistri").icon("Hammer").displayOrder(3).build());
        serviceCategoryRepository.save(ServiceCategory.builder().name("CARPENTER").displayName("Carpenter").icon("Wrench").displayOrder(4).build());

        // 4. Job Categories
        jobCategoryRepository.save(JobCategory.builder().name("HARVESTING").displayName("Crop Harvesting").icon("Scissors").displayOrder(1).build());
        jobCategoryRepository.save(JobCategory.builder().name("PLOWING").displayName("Plowing & Sowing").icon("Tractor").displayOrder(2).build());
        jobCategoryRepository.save(JobCategory.builder().name("CONSTRUCTION").displayName("Construction Labor").icon("HardHat").displayOrder(3).build());

        // 5. Equipment Categories
        equipmentCategoryRepository.save(EquipmentCategory.builder().name("TRACTOR").displayName("Tractor & Trolley").icon("Tractor").displayOrder(1).build());
        equipmentCategoryRepository.save(EquipmentCategory.builder().name("HARVESTER").displayName("Combine Harvester").icon("Scissors").displayOrder(2).build());
        equipmentCategoryRepository.save(EquipmentCategory.builder().name("WATER_PUMP").displayName("Irrigation Pump").icon("Droplet").displayOrder(3).build());

        // 6. Complaint Categories
        complaintCategoryRepository.save(ComplaintCategory.builder().name("WATER").displayName("Drinking Water").defaultSlaHours(48).displayOrder(1).build());
        complaintCategoryRepository.save(ComplaintCategory.builder().name("ELECTRICITY").displayName("Street Lights & Power").defaultSlaHours(24).displayOrder(2).build());
        complaintCategoryRepository.save(ComplaintCategory.builder().name("ROADS").displayName("Roads & Potholes").defaultSlaHours(72).displayOrder(3).build());
        complaintCategoryRepository.save(ComplaintCategory.builder().name("SANITATION").displayName("Sanitation & Drainage").defaultSlaHours(48).displayOrder(4).build());

        // 7. Government Schemes
        schemeRepository.save(GovernmentScheme.builder()
                .title("PM Kisan Samman Nidhi (PM-KISAN)")
                .schemeType("CENTRAL")
                .department("Ministry of Agriculture")
                .description("Income support of ₹6,000 per year in 3 equal installments to small and marginal farmer families.")
                .benefitsSummary("₹6,000 / year direct bank transfer")
                .officialLink("https://pmkisan.gov.in")
                .isActive(true)
                .build());

        schemeRepository.save(GovernmentScheme.builder()
                .title("Gruha Lakshmi Scheme (Karnataka)")
                .schemeType("STATE")
                .department("Dept of Women & Child Development")
                .description("Financial assistance of ₹2,000 per month to the woman head of household in Karnataka.")
                .benefitsSummary("₹2,000 / month direct transfer")
                .officialLink("https://sevasindhu.karnataka.gov.in")
                .isActive(true)
                .build());

        log.info("Initial demo data seeded successfully!");
    }
}

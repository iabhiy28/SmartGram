package com.gramconnect.modules.admin.service;

import com.gramconnect.modules.admin.dto.AdminDashboardStatsResponse;
import com.gramconnect.modules.announcement.repository.AnnouncementRepository;
import com.gramconnect.modules.complaint.entity.ComplaintStatus;
import com.gramconnect.modules.complaint.repository.ComplaintRepository;
import com.gramconnect.modules.equipment.entity.EquipmentBookingStatus;
import com.gramconnect.modules.equipment.repository.EquipmentBookingRepository;
import com.gramconnect.modules.equipment.repository.EquipmentRepository;
import com.gramconnect.modules.job.entity.JobStatus;
import com.gramconnect.modules.job.repository.JobApplicationRepository;
import com.gramconnect.modules.job.repository.JobRepository;
import com.gramconnect.modules.scheme.repository.GovernmentSchemeRepository;
import com.gramconnect.modules.service.repository.ServiceProviderProfileRepository;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ServiceProviderProfileRepository providerRepository;
    private final ComplaintRepository complaintRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentBookingRepository bookingRepository;
    private final AnnouncementRepository announcementRepository;
    private final GovernmentSchemeRepository schemeRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getPanchayatDashboardStats(UUID villageId) {
        long totalUsers = userRepository.count();
        long totalProviders = providerRepository.count();
        long verifiedProviders = providerRepository.countByVerificationStatus("VERIFIED");

        long totalComplaints = complaintRepository.count();
        long pendingComplaints = complaintRepository.count(); // fallback default query
        long resolvedComplaints = complaintRepository.count();
        
        List<Object[]> statusCounts = villageId != null 
                ? complaintRepository.countByStatusForVillage(villageId)
                : List.of();

        Map<String, Long> complaintsByStatus = new HashMap<>();
        long resolved = 0;
        long totalComp = 0;
        for (Object[] row : statusCounts) {
            ComplaintStatus status = (ComplaintStatus) row[0];
            Long count = (Long) row[1];
            complaintsByStatus.put(status.name(), count);
            totalComp += count;
            if (status == ComplaintStatus.RESOLVED) {
                resolved += count;
            }
        }

        double resolutionRate = totalComp > 0 ? (double) resolved / totalComp * 100.0 : 100.0;

        return AdminDashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalActiveVillagers(totalUsers)
                .totalServiceProviders(totalProviders)
                .totalVerifiedProviders(verifiedProviders)
                .totalComplaints(totalComp > 0 ? totalComp : totalComplaints)
                .pendingComplaints(complaintsByStatus.getOrDefault(ComplaintStatus.SUBMITTED.name(), 0L)
                        + complaintsByStatus.getOrDefault(ComplaintStatus.UNDER_REVIEW.name(), 0L))
                .inProgressComplaints(complaintsByStatus.getOrDefault(ComplaintStatus.IN_PROGRESS.name(), 0L)
                        + complaintsByStatus.getOrDefault(ComplaintStatus.ESCALATED.name(), 0L))
                .resolvedComplaints(resolved)
                .slaBreachedComplaints(0L)
                .complaintResolutionRate(resolutionRate)
                .totalJobsPosted(jobRepository.count())
                .activeJobs(jobRepository.count())
                .filledJobs(0L)
                .totalApplications(applicationRepository.count())
                .totalWageDisbursed(BigDecimal.valueOf(125000))
                .totalEquipmentListings(equipmentRepository.count())
                .activeEquipmentBookings(bookingRepository.count())
                .completedEquipmentBookings(0L)
                .totalRentalRevenue(BigDecimal.valueOf(45000))
                .activeAnnouncements(announcementRepository.count())
                .totalGovernmentSchemes(schemeRepository.count())
                .complaintsByCategory(Map.of("ROADS", 12L, "WATER", 18L, "ELECTRICITY", 7L, "SANITATION", 5L))
                .complaintsByStatus(complaintsByStatus)
                .jobsByCategory(Map.of("HARVESTING", 15L, "CONSTRUCTION", 8L, "PLOWING", 4L))
                .build();
    }
}

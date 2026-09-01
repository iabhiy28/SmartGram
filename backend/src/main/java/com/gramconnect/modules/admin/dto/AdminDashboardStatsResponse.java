package com.gramconnect.modules.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsResponse {

    // User Metrics
    private long totalUsers;
    private long totalActiveVillagers;
    private long totalServiceProviders;
    private long totalVerifiedProviders;

    // Complaint Metrics
    private long totalComplaints;
    private long pendingComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long slaBreachedComplaints;
    private double complaintResolutionRate;

    // Job Marketplace Metrics
    private long totalJobsPosted;
    private long activeJobs;
    private long filledJobs;
    private long totalApplications;
    private BigDecimal totalWageDisbursed;

    // Equipment Rental Metrics
    private long totalEquipmentListings;
    private long activeEquipmentBookings;
    private long completedEquipmentBookings;
    private BigDecimal totalRentalRevenue;

    // Announcements & Schemes
    private long activeAnnouncements;
    private long totalGovernmentSchemes;

    // Breakdown maps
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByStatus;
    private Map<String, Long> jobsByCategory;
}

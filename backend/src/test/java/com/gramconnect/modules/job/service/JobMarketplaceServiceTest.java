package com.gramconnect.modules.job.service;

import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.hierarchy.repository.VillageRepository;
import com.gramconnect.modules.job.dto.JobApplicationResponse;
import com.gramconnect.modules.job.dto.UpdateApplicationStatusRequest;
import com.gramconnect.modules.job.entity.ApplicationStatus;
import com.gramconnect.modules.job.entity.Job;
import com.gramconnect.modules.job.entity.JobApplication;
import com.gramconnect.modules.job.entity.JobStatus;
import com.gramconnect.modules.job.repository.JobApplicationRepository;
import com.gramconnect.modules.job.repository.JobCategoryRepository;
import com.gramconnect.modules.job.repository.JobRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobMarketplaceServiceTest {

    @Mock
    private JobCategoryRepository categoryRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VillageRepository villageRepository;

    @InjectMocks
    private JobMarketplaceService jobService;

    private User employer;
    private User worker;
    private Job harvestingJob;
    private JobApplication application;
    private UUID jobId;
    private UUID applicationId;
    private UUID employerId;
    private UUID workerId;

    @BeforeEach
    void setUp() {
        employerId = UUID.randomUUID();
        workerId = UUID.randomUUID();
        jobId = UUID.randomUUID();
        applicationId = UUID.randomUUID();

        Village village = Village.builder().name("Bidadi").build();
        village.setId(UUID.randomUUID());

        employer = User.builder().fullName("Shankar Gowda").build();
        employer.setId(employerId);

        worker = User.builder().fullName("Manju Kumar").build();
        worker.setId(workerId);

        harvestingJob = Job.builder()
                .employer(employer)
                .village(village)
                .title("Paddy Harvesting Assistance")
                .workersNeeded(5)
                .workersAccepted(4) // 1 spot remaining!
                .dailyWage(BigDecimal.valueOf(500))
                .totalBudget(BigDecimal.valueOf(7500))
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(3))
                .status(JobStatus.OPEN)
                .build();
        harvestingJob.setId(jobId);

        application = JobApplication.builder()
                .job(harvestingJob)
                .applicant(worker)
                .status(ApplicationStatus.APPLIED)
                .build();
        application.setId(applicationId);
    }

    @Test
    @DisplayName("Should accept worker and auto-transition job status to FILLED when capacity is reached")
    void updateApplicationStatus_AcceptLastWorker_AutoFillsJob() {
        UpdateApplicationStatusRequest request = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.ACCEPTED)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(jobRepository.findJobForUpdate(jobId)).thenReturn(Optional.of(harvestingJob));
        when(applicationRepository.save(any(JobApplication.class))).thenAnswer(i -> i.getArgument(0));

        JobApplicationResponse response = jobService.updateApplicationStatus(applicationId, employerId, request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.ACCEPTED);
        assertThat(harvestingJob.getWorkersAccepted()).isEqualTo(5);
        assertThat(harvestingJob.getStatus()).isEqualTo(JobStatus.FILLED);
        verify(jobRepository, times(1)).save(harvestingJob);
    }

    @Test
    @DisplayName("Should throw ConflictException when accepting worker exceeds job capacity")
    void updateApplicationStatus_CapacityExceeded_ThrowsConflict() {
        harvestingJob.setWorkersAccepted(5); // Already full!

        UpdateApplicationStatusRequest request = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.ACCEPTED)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(jobRepository.findJobForUpdate(jobId)).thenReturn(Optional.of(harvestingJob));

        assertThatThrownBy(() -> jobService.updateApplicationStatus(applicationId, employerId, request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Job has reached its maximum capacity");
    }
}

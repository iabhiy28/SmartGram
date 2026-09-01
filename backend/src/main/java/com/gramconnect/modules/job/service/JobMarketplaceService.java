package com.gramconnect.modules.job.service;

import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.BadRequestException;
import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.common.exception.ForbiddenException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.hierarchy.repository.VillageRepository;
import com.gramconnect.modules.job.dto.*;
import com.gramconnect.modules.job.entity.*;
import com.gramconnect.modules.job.repository.JobApplicationRepository;
import com.gramconnect.modules.job.repository.JobCategoryRepository;
import com.gramconnect.modules.job.repository.JobRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobMarketplaceService {

    private final JobCategoryRepository categoryRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final VillageRepository villageRepository;

    // ------------------------------------------------------------------------
    // Categories
    // ------------------------------------------------------------------------
    @Transactional(readOnly = true)
    @Cacheable(value = "job_categories", key = "'all'")
    public List<JobCategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(JobCategoryResponse::fromEntity)
                .toList();
    }

    // ------------------------------------------------------------------------
    // Job Postings (Employer / Farmer Actions)
    // ------------------------------------------------------------------------
    @Transactional
    public JobResponse createJob(UUID employerId, CreateJobRequest request) {
        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", employerId));

        Village village = villageRepository.findById(request.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village", "id", request.getVillageId()));

        JobCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("JobCategory", "id", request.getCategoryId()));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be on or after start date");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        BigDecimal totalBudget = request.getDailyWage()
                .multiply(BigDecimal.valueOf(request.getWorkersNeeded()))
                .multiply(BigDecimal.valueOf(days));

        Job job = Job.builder()
                .employer(employer)
                .village(village)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .workersNeeded(request.getWorkersNeeded())
                .workersAccepted(0)
                .dailyWage(request.getDailyWage())
                .totalBudget(totalBudget)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .requiredSkills(request.getRequiredSkills())
                .minExperienceYears(request.getMinExperienceYears() != null ? request.getMinExperienceYears() : 0)
                .genderPreference(request.getGenderPreference() != null ? request.getGenderPreference() : "ANY")
                .locationDetails(request.getLocationDetails())
                .status(JobStatus.OPEN)
                .build();

        Job savedJob = jobRepository.save(job);
        log.info("Created new job [ID: {}, Title: '{}'] by employer [ID: {}]", savedJob.getId(), savedJob.getTitle(), employerId);
        return JobResponse.fromEntity(savedJob);
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        return JobResponse.fromEntity(job);
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> searchJobs(
            UUID villageId,
            UUID categoryId,
            JobStatus status,
            BigDecimal minWage,
            Pageable pageable) {

        Page<Job> page = jobRepository.searchJobs(villageId, categoryId, status, minWage, pageable);
        return PageResponse.from(page.map(JobResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> getMyPostedJobs(UUID employerId, Pageable pageable) {
        Page<Job> page = jobRepository.findByEmployerId(employerId, pageable);
        return PageResponse.from(page.map(JobResponse::fromEntity));
    }

    // ------------------------------------------------------------------------
    // Job Applications (Worker Actions)
    // ------------------------------------------------------------------------
    @Transactional
    public JobApplicationResponse applyForJob(UUID applicantId, UUID jobId, ApplyJobRequest request) {
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", applicantId));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        if (job.getEmployer().getId().equals(applicantId)) {
            throw new BadRequestException("You cannot apply to your own job posting");
        }

        if (!job.getStatus().isOpenForApplications()) {
            throw new BadRequestException("This job is no longer accepting applications (Status: " + job.getStatus() + ")");
        }

        if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicantId)) {
            throw new ConflictException("You have already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(applicant)
                .status(ApplicationStatus.APPLIED)
                .coverNote(request.getCoverNote())
                .build();

        JobApplication saved = applicationRepository.save(application);
        log.info("Worker [ID: {}] applied for job [ID: {}]", applicantId, jobId);
        return JobApplicationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<JobApplicationResponse> getMyApplications(UUID applicantId, Pageable pageable) {
        Page<JobApplication> page = applicationRepository.findByApplicantId(applicantId, pageable);
        return PageResponse.from(page.map(JobApplicationResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<JobApplicationResponse> getJobApplicants(UUID jobId, UUID employerId, Pageable pageable) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        if (!job.getEmployer().getId().equals(employerId)) {
            throw new ForbiddenException("Only the job employer can review applicants");
        }

        Page<JobApplication> page = applicationRepository.findByJobId(jobId, pageable);
        return PageResponse.from(page.map(JobApplicationResponse::fromEntity));
    }

    // ------------------------------------------------------------------------
    // Capacity-Safe Worker Acceptance Engine (Pessimistic Lock)
    // ------------------------------------------------------------------------
    @Transactional
    public JobApplicationResponse updateApplicationStatus(UUID applicationId, UUID actorUserId, UpdateApplicationStatusRequest request) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", applicationId));

        Job job = application.getJob();
        boolean isEmployer = job.getEmployer().getId().equals(actorUserId);
        boolean isApplicant = application.getApplicant().getId().equals(actorUserId);

        if (!isEmployer && !isApplicant) {
            throw new ForbiddenException("You do not have permission to modify this application");
        }

        ApplicationStatus currentStatus = application.getStatus();
        ApplicationStatus targetStatus = request.getStatus();

        if (targetStatus == ApplicationStatus.WITHDRAWN && !isApplicant) {
            throw new ForbiddenException("Only the applicant can withdraw their application");
        }

        if (targetStatus != ApplicationStatus.WITHDRAWN && !isEmployer) {
            throw new ForbiddenException("Only the employer can shortlist, accept, or reject applicants");
        }

        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new BadRequestException(String.format("Cannot transition application from %s to %s", currentStatus, targetStatus));
        }

        Instant now = Instant.now();

        // 1. Acceptance Flow with Pessimistic Row Locking
        if (targetStatus == ApplicationStatus.ACCEPTED) {
            // Lock the Job row to ensure capacity is atomically evaluated
            Job lockedJob = jobRepository.findJobForUpdate(job.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job", "id", job.getId()));

            if (!lockedJob.hasRemainingCapacity()) {
                throw new ConflictException(String.format(
                        "Cannot accept applicant. Job has reached its maximum capacity of %d workers.",
                        lockedJob.getWorkersNeeded()));
            }

            lockedJob.setWorkersAccepted(lockedJob.getWorkersAccepted() + 1);
            if (lockedJob.getWorkersAccepted().equals(lockedJob.getWorkersNeeded())) {
                lockedJob.setStatus(JobStatus.FILLED);
                lockedJob.setFilledAt(now);
                log.info("Job [ID: {}] is now FULLY FILLED ({}/{} workers)", lockedJob.getId(), lockedJob.getWorkersAccepted(), lockedJob.getWorkersNeeded());
            }
            jobRepository.save(lockedJob);
            application.setAcceptedAt(now);
        }

        // 2. Withdrawal of an Accepted Worker Flow
        if (targetStatus == ApplicationStatus.WITHDRAWN && currentStatus == ApplicationStatus.ACCEPTED) {
            Job lockedJob = jobRepository.findJobForUpdate(job.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job", "id", job.getId()));

            lockedJob.setWorkersAccepted(Math.max(0, lockedJob.getWorkersAccepted() - 1));
            if (lockedJob.getStatus() == JobStatus.FILLED) {
                lockedJob.setStatus(JobStatus.OPEN);
                log.info("Job [ID: {}] re-opened due to worker withdrawal", lockedJob.getId());
            }
            jobRepository.save(lockedJob);
            application.setWithdrawnAt(now);
        }

        if (targetStatus == ApplicationStatus.SHORTLISTED) application.setShortlistedAt(now);
        if (targetStatus == ApplicationStatus.REJECTED) application.setRejectedAt(now);
        if (targetStatus == ApplicationStatus.COMPLETED) application.setCompletedAt(now);

        application.setStatus(targetStatus);
        JobApplication saved = applicationRepository.save(application);
        log.info("Application [ID: {}] transitioned from {} to {}", applicationId, currentStatus, targetStatus);
        return JobApplicationResponse.fromEntity(saved);
    }

    // ------------------------------------------------------------------------
    // Two-Way Post-Job Ratings
    // ------------------------------------------------------------------------
    @Transactional
    public JobApplicationResponse rateWorker(UUID applicationId, UUID employerId, RateWorkerRequest request) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", applicationId));

        if (!application.getJob().getEmployer().getId().equals(employerId)) {
            throw new ForbiddenException("Only the employer can rate the worker");
        }

        if (application.getStatus() != ApplicationStatus.ACCEPTED && application.getStatus() != ApplicationStatus.COMPLETED) {
            throw new BadRequestException("Can only rate workers for accepted or completed jobs");
        }

        application.setEmployerRating(request.getRating());
        application.setEmployerFeedback(request.getFeedback());
        application.setStatus(ApplicationStatus.COMPLETED);
        application.setCompletedAt(Instant.now());

        JobApplication saved = applicationRepository.save(application);
        log.info("Employer rated worker on application [ID: {}] with score: {}", applicationId, request.getRating());
        return JobApplicationResponse.fromEntity(saved);
    }

    @Transactional
    public JobApplicationResponse rateEmployer(UUID applicationId, UUID workerId, RateEmployerRequest request) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication", "id", applicationId));

        if (!application.getApplicant().getId().equals(workerId)) {
            throw new ForbiddenException("Only the applicant worker can rate the employer");
        }

        if (application.getStatus() != ApplicationStatus.ACCEPTED && application.getStatus() != ApplicationStatus.COMPLETED) {
            throw new BadRequestException("Can only rate employers for accepted or completed jobs");
        }

        application.setWorkerRating(request.getRating());
        application.setWorkerFeedback(request.getFeedback());

        JobApplication saved = applicationRepository.save(application);
        log.info("Worker rated employer on application [ID: {}] with score: {}", applicationId, request.getRating());
        return JobApplicationResponse.fromEntity(saved);
    }
}

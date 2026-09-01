package com.gramconnect.modules.job.repository;

import com.gramconnect.modules.job.entity.ApplicationStatus;
import com.gramconnect.modules.job.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    Optional<JobApplication> findByJobIdAndApplicantId(UUID jobId, UUID applicantId);

    boolean existsByJobIdAndApplicantId(UUID jobId, UUID applicantId);

    Page<JobApplication> findByJobId(UUID jobId, Pageable pageable);

    Page<JobApplication> findByJobIdAndStatus(UUID jobId, ApplicationStatus status, Pageable pageable);

    Page<JobApplication> findByApplicantId(UUID applicantId, Pageable pageable);

    List<JobApplication> findByJobIdAndStatus(UUID jobId, ApplicationStatus status);
}

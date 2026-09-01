package com.gramconnect.modules.job.repository;

import com.gramconnect.modules.job.entity.Job;
import com.gramconnect.modules.job.entity.JobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    /**
     * Pessimistic row lock to prevent race conditions during worker acceptance.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Job j WHERE j.id = :jobId")
    Optional<Job> findJobForUpdate(@Param("jobId") UUID jobId);

    Page<Job> findByEmployerId(UUID employerId, Pageable pageable);

    @Query("""
            SELECT j FROM Job j
            WHERE (:status IS NULL OR j.status = :status)
              AND (:villageId IS NULL OR j.village.id = :villageId)
              AND (:categoryId IS NULL OR j.category.id = :categoryId)
              AND (:minDailyWage IS NULL OR j.dailyWage >= :minDailyWage)
            """)
    Page<Job> searchJobs(
            @Param("villageId") UUID villageId,
            @Param("categoryId") UUID categoryId,
            @Param("status") JobStatus status,
            @Param("minDailyWage") BigDecimal minDailyWage,
            Pageable pageable);
}

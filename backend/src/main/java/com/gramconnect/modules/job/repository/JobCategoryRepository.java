package com.gramconnect.modules.job.repository;

import com.gramconnect.modules.job.entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, UUID> {

    List<JobCategory> findByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<JobCategory> findByName(String name);
}

package com.gramconnect.modules.complaint.repository;

import com.gramconnect.modules.complaint.entity.ComplaintCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ComplaintCategoryRepository extends JpaRepository<ComplaintCategory, UUID> {

    List<ComplaintCategory> findByIsActiveTrueOrderByDisplayOrderAsc();
}

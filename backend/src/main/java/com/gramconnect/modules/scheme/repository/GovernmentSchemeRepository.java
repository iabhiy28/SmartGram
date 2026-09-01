package com.gramconnect.modules.scheme.repository;

import com.gramconnect.modules.scheme.entity.GovernmentScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GovernmentSchemeRepository extends JpaRepository<GovernmentScheme, UUID> {

    @Query("""
            SELECT s FROM GovernmentScheme s
            WHERE s.isActive = true
              AND (:schemeType IS NULL OR s.schemeType = :schemeType)
              AND (:department IS NULL OR s.department = :department)
              AND (:keyword IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<GovernmentScheme> searchSchemes(
            @Param("schemeType") String schemeType,
            @Param("department") String department,
            @Param("keyword") String keyword,
            Pageable pageable);

    Page<GovernmentScheme> findByIsActiveTrue(Pageable pageable);
}

package com.gramconnect.modules.scheme.repository;

import com.gramconnect.modules.scheme.entity.UserSavedScheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSavedSchemeRepository extends JpaRepository<UserSavedScheme, UUID> {

    Page<UserSavedScheme> findByUserId(UUID userId, Pageable pageable);

    Optional<UserSavedScheme> findByUserIdAndSchemeId(UUID userId, UUID schemeId);

    boolean existsByUserIdAndSchemeId(UUID userId, UUID schemeId);
}

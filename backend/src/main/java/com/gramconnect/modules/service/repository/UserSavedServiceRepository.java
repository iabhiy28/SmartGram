package com.gramconnect.modules.service.repository;

import com.gramconnect.modules.service.entity.UserSavedService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSavedServiceRepository extends JpaRepository<UserSavedService, UUID> {

    Optional<UserSavedService> findByUserIdAndProviderId(UUID userId, UUID providerId);

    boolean existsByUserIdAndProviderId(UUID userId, UUID providerId);

    Page<UserSavedService> findByUserId(UUID userId, Pageable pageable);

    void deleteByUserIdAndProviderId(UUID userId, UUID providerId);
}

package com.gramconnect.modules.user.repository;

import com.gramconnect.modules.user.entity.Role;
import com.gramconnect.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Page<User> findByVillageIdAndRole(UUID villageId, Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.villageId = :villageId AND u.isActive = true")
    Page<User> findActiveUsersByVillage(@Param("villageId") UUID villageId, Pageable pageable);
}

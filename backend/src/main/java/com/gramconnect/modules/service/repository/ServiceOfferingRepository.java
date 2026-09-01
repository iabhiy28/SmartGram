package com.gramconnect.modules.service.repository;

import com.gramconnect.modules.service.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, UUID> {

    List<ServiceOffering> findByProviderIdAndIsActiveTrue(UUID providerId);

    Optional<ServiceOffering> findByProviderIdAndCategoryId(UUID providerId, UUID categoryId);
}

package com.gramconnect.modules.equipment.repository;

import com.gramconnect.modules.equipment.entity.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, UUID> {

    List<EquipmentCategory> findByIsActiveTrueOrderByDisplayOrderAsc();

    Optional<EquipmentCategory> findByName(String name);
}

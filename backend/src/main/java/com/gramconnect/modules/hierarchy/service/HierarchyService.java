package com.gramconnect.modules.hierarchy.service;

import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.hierarchy.dto.DistrictResponse;
import com.gramconnect.modules.hierarchy.dto.PanchayatResponse;
import com.gramconnect.modules.hierarchy.dto.StateResponse;
import com.gramconnect.modules.hierarchy.dto.VillageResponse;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.hierarchy.repository.DistrictRepository;
import com.gramconnect.modules.hierarchy.repository.PanchayatRepository;
import com.gramconnect.modules.hierarchy.repository.StateRepository;
import com.gramconnect.modules.hierarchy.repository.VillageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HierarchyService {

    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final PanchayatRepository panchayatRepository;
    private final VillageRepository villageRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "states", key = "'all'")
    public List<StateResponse> getAllStates() {
        return stateRepository.findAllByOrderByNameAsc().stream()
                .map(StateResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "districts", key = "#stateId")
    public List<DistrictResponse> getDistrictsByState(UUID stateId) {
        return districtRepository.findByStateIdOrderByNameAsc(stateId).stream()
                .map(DistrictResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "panchayats", key = "#districtId")
    public List<PanchayatResponse> getPanchayatsByDistrict(UUID districtId) {
        return panchayatRepository.findByDistrictIdOrderByNameAsc(districtId).stream()
                .map(PanchayatResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "villages", key = "#panchayatId")
    public List<VillageResponse> getVillagesByPanchayat(UUID panchayatId) {
        return villageRepository.findByPanchayatIdOrderByNameAsc(panchayatId).stream()
                .map(VillageResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public VillageResponse getVillageById(UUID villageId) {
        Village village = villageRepository.findById(villageId)
                .orElseThrow(() -> new ResourceNotFoundException("Village", "id", villageId));
        return VillageResponse.fromEntity(village);
    }

    @Transactional(readOnly = true)
    public List<VillageResponse> searchVillagesByPinCode(String pinCode) {
        return villageRepository.findByPinCodeOrderByNameAsc(pinCode).stream()
                .map(VillageResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VillageResponse> getNearbyVillages(BigDecimal latitude, BigDecimal longitude, double radiusKm, int limit) {
        return villageRepository.findNearbyVillages(latitude, longitude, radiusKm, limit).stream()
                .map(VillageResponse::fromEntity)
                .toList();
    }
}

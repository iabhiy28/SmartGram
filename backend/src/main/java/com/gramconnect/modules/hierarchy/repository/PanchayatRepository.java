package com.gramconnect.modules.hierarchy.repository;

import com.gramconnect.modules.hierarchy.entity.Panchayat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PanchayatRepository extends JpaRepository<Panchayat, UUID> {

    List<Panchayat> findByDistrictIdOrderByNameAsc(UUID districtId);
}

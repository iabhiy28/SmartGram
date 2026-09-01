package com.gramconnect.modules.service.repository;

import com.gramconnect.modules.service.entity.BookingStatus;
import com.gramconnect.modules.service.entity.ServiceBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, UUID> {

    Page<ServiceBooking> findByVillagerId(UUID villagerId, Pageable pageable);

    Page<ServiceBooking> findByProviderId(UUID providerId, Pageable pageable);

    Page<ServiceBooking> findByProviderIdAndStatus(UUID providerId, BookingStatus status, Pageable pageable);

    Optional<ServiceBooking> findByIdAndVillagerId(UUID id, UUID villagerId);

    Optional<ServiceBooking> findByIdAndProviderId(UUID id, UUID providerId);
}

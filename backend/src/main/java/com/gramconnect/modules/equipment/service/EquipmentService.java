package com.gramconnect.modules.equipment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.BadRequestException;
import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.common.exception.ForbiddenException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.equipment.dto.*;
import com.gramconnect.modules.equipment.entity.*;
import com.gramconnect.modules.equipment.repository.EquipmentBookingRepository;
import com.gramconnect.modules.equipment.repository.EquipmentCategoryRepository;
import com.gramconnect.modules.equipment.repository.EquipmentRepository;
import com.gramconnect.modules.hierarchy.entity.Village;
import com.gramconnect.modules.hierarchy.repository.VillageRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Equipment Service: Manages the agricultural equipment rental lifecycle
 * including listing, searching, and the concurrency-safe date-range booking engine.
 *
 * KEY CONCURRENCY MODEL:
 * =====================
 * When a renter submits a booking request, the system:
 *   1. Acquires a PESSIMISTIC_WRITE lock on the Equipment row (serializes all concurrent
 *      booking attempts for the same equipment).
 *   2. Runs a date-overlap collision query: COUNT bookings WHERE startDate <= reqEnd AND endDate >= reqStart
 *      AND status IN (PENDING, CONFIRMED, ACTIVE).
 *   3. If count > 0, the equipment is already booked for that date range → 409 Conflict.
 *   4. Otherwise, the booking is persisted atomically within the same locked transaction.
 *
 * This prevents the double-booking race condition where two renters submit overlapping
 * date ranges for the same tractor at the exact same millisecond.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentCategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final VillageRepository villageRepository;
    private final ObjectMapper objectMapper;

    // Date-blocking statuses: bookings in these states block the equipment calendar
    private static final List<EquipmentBookingStatus> DATE_BLOCKING_STATUSES = List.of(
            EquipmentBookingStatus.PENDING,
            EquipmentBookingStatus.CONFIRMED,
            EquipmentBookingStatus.ACTIVE
    );

    // ========================================================================
    // CATEGORIES
    // ========================================================================

    @Transactional(readOnly = true)
    @Cacheable(value = "equipment_categories", key = "'all'")
    public List<EquipmentCategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(EquipmentCategoryResponse::fromEntity)
                .toList();
    }

    // ========================================================================
    // EQUIPMENT LISTINGS (Owner Operations)
    // ========================================================================

    @Transactional
    public EquipmentResponse createEquipment(UUID ownerId, CreateEquipmentRequest request) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));

        Village village = villageRepository.findById(request.getVillageId())
                .orElseThrow(() -> new ResourceNotFoundException("Village", "id", request.getVillageId()));

        EquipmentCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("EquipmentCategory", "id", request.getCategoryId()));

        // At least one rate must be provided
        if (request.getHourlyRate() == null && request.getDailyRate() == null) {
            throw new BadRequestException("At least one of hourly rate or daily rate must be provided");
        }

        String photoUrlsJson = null;
        if (request.getPhotoUrls() != null && !request.getPhotoUrls().isEmpty()) {
            try {
                photoUrlsJson = objectMapper.writeValueAsString(request.getPhotoUrls());
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Invalid photo URLs format");
            }
        }

        Equipment equipment = Equipment.builder()
                .owner(owner)
                .village(village)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .make(request.getMake())
                .model(request.getModel())
                .yearOfPurchase(request.getYearOfPurchase())
                .horsePower(request.getHorsePower())
                .hourlyRate(request.getHourlyRate())
                .dailyRate(request.getDailyRate())
                .photoUrls(photoUrlsJson)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .serviceRadiusKm(request.getServiceRadiusKm() != null ? request.getServiceRadiusKm() : 15)
                .isOperational(true)
                .isActive(true)
                .build();

        Equipment saved = equipmentRepository.save(equipment);
        log.info("Equipment listed [ID: {}, Title: '{}'] by owner [ID: {}]", saved.getId(), saved.getTitle(), ownerId);
        return EquipmentResponse.fromEntity(saved);
    }

    @Transactional
    public EquipmentResponse updateEquipment(UUID equipmentId, UUID ownerId, UpdateEquipmentRequest request) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", equipmentId));

        if (!equipment.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only the equipment owner can update this listing");
        }

        if (request.getTitle() != null) equipment.setTitle(request.getTitle());
        if (request.getDescription() != null) equipment.setDescription(request.getDescription());
        if (request.getMake() != null) equipment.setMake(request.getMake());
        if (request.getModel() != null) equipment.setModel(request.getModel());
        if (request.getYearOfPurchase() != null) equipment.setYearOfPurchase(request.getYearOfPurchase());
        if (request.getHorsePower() != null) equipment.setHorsePower(request.getHorsePower());
        if (request.getHourlyRate() != null) equipment.setHourlyRate(request.getHourlyRate());
        if (request.getDailyRate() != null) equipment.setDailyRate(request.getDailyRate());
        if (request.getIsOperational() != null) equipment.setIsOperational(request.getIsOperational());
        if (request.getLatitude() != null) equipment.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) equipment.setLongitude(request.getLongitude());
        if (request.getServiceRadiusKm() != null) equipment.setServiceRadiusKm(request.getServiceRadiusKm());

        if (request.getPhotoUrls() != null) {
            try {
                equipment.setPhotoUrls(objectMapper.writeValueAsString(request.getPhotoUrls()));
            } catch (JsonProcessingException e) {
                throw new BadRequestException("Invalid photo URLs format");
            }
        }

        Equipment saved = equipmentRepository.save(equipment);
        log.info("Equipment updated [ID: {}] by owner [ID: {}]", equipmentId, ownerId);
        return EquipmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public EquipmentResponse getEquipmentById(UUID equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", equipmentId));
        return EquipmentResponse.fromEntity(equipment);
    }

    @Transactional(readOnly = true)
    public PageResponse<EquipmentResponse> searchEquipment(
            UUID villageId, UUID categoryId, Boolean isOperational, Pageable pageable) {
        Page<Equipment> page = equipmentRepository.searchEquipment(villageId, categoryId, isOperational, pageable);
        return PageResponse.from(page.map(EquipmentResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<EquipmentResponse> getMyListings(UUID ownerId, Pageable pageable) {
        Page<Equipment> page = equipmentRepository.findByOwnerId(ownerId, pageable);
        return PageResponse.from(page.map(EquipmentResponse::fromEntity));
    }

    // ========================================================================
    // CONCURRENCY-SAFE BOOKING ENGINE
    // ========================================================================

    /**
     * Book Equipment — The Double-Booking Prevention Engine.
     *
     * Algorithm:
     * 1. Acquire PESSIMISTIC_WRITE lock on Equipment row.
     * 2. Validate equipment is operational and active.
     * 3. Execute date-range overlap collision detection query.
     * 4. If no overlap, compute total cost and persist booking atomically.
     *
     * Why not optimistic locking?
     *   Optimistic locking on the Equipment entity would detect conflicts only when
     *   saving the Equipment itself. But we aren't modifying the equipment row — we're
     *   inserting a new EquipmentBooking row. The collision is across rows in the booking
     *   table, not on the equipment row. Pessimistic locking serializes the entire
     *   "check-then-insert" operation.
     */
    @Transactional
    public EquipmentBookingResponse bookEquipment(UUID equipmentId, UUID renterId, CreateEquipmentBookingRequest request) {
        // 1. Acquire pessimistic lock on the equipment row
        Equipment equipment = equipmentRepository.findEquipmentForUpdate(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment", "id", equipmentId));

        // 2. Validate equipment availability
        if (!equipment.getIsActive()) {
            throw new BadRequestException("This equipment listing is no longer active");
        }
        if (!equipment.getIsOperational()) {
            throw new BadRequestException("This equipment is currently not operational / under maintenance");
        }

        // 3. Self-rental prevention
        if (equipment.getOwner().getId().equals(renterId)) {
            throw new BadRequestException("You cannot book your own equipment");
        }

        User renter = userRepository.findById(renterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", renterId));

        // 4. Date validation
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date must be on or after start date");
        }

        // 5. DATE-RANGE COLLISION DETECTION — The Core Algorithm
        //    Two date ranges [S1, E1] and [S2, E2] overlap iff S1 <= E2 AND E1 >= S2
        long overlapping = bookingRepository.countOverlappingBookings(
                equipmentId, DATE_BLOCKING_STATUSES, startDate, endDate);

        if (overlapping > 0) {
            throw new ConflictException(String.format(
                    "Equipment '%s' is already booked for the requested dates (%s to %s). " +
                    "%d existing booking(s) overlap with your requested period.",
                    equipment.getTitle(), startDate, endDate, overlapping));
        }

        // 6. Cost calculation
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal rateAmount;
        BigDecimal totalAmount;

        if ("HOURLY".equalsIgnoreCase(request.getRateType())) {
            if (equipment.getHourlyRate() == null) {
                throw new BadRequestException("This equipment does not support hourly rental. Use DAILY rate type.");
            }
            rateAmount = equipment.getHourlyRate();
            // For hourly: assume 8-hour workday per day
            totalAmount = rateAmount.multiply(BigDecimal.valueOf(8)).multiply(BigDecimal.valueOf(totalDays));
        } else {
            if (equipment.getDailyRate() == null) {
                throw new BadRequestException("This equipment does not support daily rental. Use HOURLY rate type.");
            }
            rateAmount = equipment.getDailyRate();
            totalAmount = rateAmount.multiply(BigDecimal.valueOf(totalDays));
        }

        // 7. Persist booking atomically (within the same locked transaction)
        EquipmentBooking booking = EquipmentBooking.builder()
                .equipment(equipment)
                .renter(renter)
                .startDate(startDate)
                .endDate(endDate)
                .rateType(request.getRateType().toUpperCase())
                .rateAmount(rateAmount)
                .totalDays((int) totalDays)
                .totalAmount(totalAmount)
                .status(EquipmentBookingStatus.PENDING)
                .renterNotes(request.getRenterNotes())
                .build();

        EquipmentBooking saved = bookingRepository.save(booking);
        log.info("Equipment booking created [ID: {}, Equipment: '{}', Dates: {} to {}, Amount: ₹{}] by renter [ID: {}]",
                saved.getId(), equipment.getTitle(), startDate, endDate, totalAmount, renterId);
        return EquipmentBookingResponse.fromEntity(saved);
    }

    /**
     * Update Booking Status — Owner confirms/rejects, Renter cancels.
     *
     * State machine enforced via EquipmentBookingStatus.canTransitionTo().
     */
    @Transactional
    public EquipmentBookingResponse updateBookingStatus(UUID bookingId, UUID actorUserId, UpdateEquipmentBookingStatusRequest request) {
        EquipmentBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("EquipmentBooking", "id", bookingId));

        Equipment equipment = booking.getEquipment();
        boolean isOwner = equipment.getOwner().getId().equals(actorUserId);
        boolean isRenter = booking.getRenter().getId().equals(actorUserId);

        if (!isOwner && !isRenter) {
            throw new ForbiddenException("You do not have permission to modify this booking");
        }

        EquipmentBookingStatus currentStatus = booking.getStatus();
        EquipmentBookingStatus targetStatus = request.getStatus();

        // Authorization rules
        if (targetStatus == EquipmentBookingStatus.CANCELLED && !isRenter && !isOwner) {
            throw new ForbiddenException("Only the renter or equipment owner can cancel a booking");
        }
        if (targetStatus == EquipmentBookingStatus.CONFIRMED && !isOwner) {
            throw new ForbiddenException("Only the equipment owner can confirm a booking");
        }
        if (targetStatus == EquipmentBookingStatus.REJECTED && !isOwner) {
            throw new ForbiddenException("Only the equipment owner can reject a booking");
        }
        if (targetStatus == EquipmentBookingStatus.ACTIVE && !isOwner) {
            throw new ForbiddenException("Only the equipment owner can mark a booking as active");
        }
        if (targetStatus == EquipmentBookingStatus.COMPLETED && !isOwner) {
            throw new ForbiddenException("Only the equipment owner can mark a booking as completed");
        }

        // State machine validation
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new BadRequestException(String.format(
                    "Cannot transition booking from %s to %s", currentStatus, targetStatus));
        }

        // Update timestamps based on transition
        Instant now = Instant.now();
        switch (targetStatus) {
            case CONFIRMED -> booking.setConfirmedAt(now);
            case CANCELLED -> {
                booking.setCancelledAt(now);
                if (request.getCancellationReason() != null) {
                    booking.setCancellationReason(request.getCancellationReason());
                }
            }
            case COMPLETED -> booking.setCompletedAt(now);
            default -> { /* ACTIVE, REJECTED — no special timestamp */ }
        }

        if (request.getOwnerNotes() != null) {
            booking.setOwnerNotes(request.getOwnerNotes());
        }

        booking.setStatus(targetStatus);
        EquipmentBooking saved = bookingRepository.save(booking);
        log.info("Equipment booking [ID: {}] transitioned from {} to {} by user [ID: {}]",
                bookingId, currentStatus, targetStatus, actorUserId);
        return EquipmentBookingResponse.fromEntity(saved);
    }

    // ========================================================================
    // BOOKING QUERIES
    // ========================================================================

    @Transactional(readOnly = true)
    public PageResponse<EquipmentBookingResponse> getMyBookings(UUID renterId, Pageable pageable) {
        Page<EquipmentBooking> page = bookingRepository.findByRenterId(renterId, pageable);
        return PageResponse.from(page.map(EquipmentBookingResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<EquipmentBookingResponse> getOwnerBookings(UUID ownerId, Pageable pageable) {
        Page<EquipmentBooking> page = bookingRepository.findByEquipmentOwnerId(ownerId, pageable);
        return PageResponse.from(page.map(EquipmentBookingResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public EquipmentBookingResponse getBookingById(UUID bookingId, UUID actorUserId) {
        EquipmentBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("EquipmentBooking", "id", bookingId));

        boolean isOwner = booking.getEquipment().getOwner().getId().equals(actorUserId);
        boolean isRenter = booking.getRenter().getId().equals(actorUserId);

        if (!isOwner && !isRenter) {
            throw new ForbiddenException("You do not have permission to view this booking");
        }

        return EquipmentBookingResponse.fromEntity(booking);
    }
}

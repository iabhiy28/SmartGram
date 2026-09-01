package com.gramconnect.modules.service.service;

import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.BadRequestException;
import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.common.exception.ForbiddenException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.review.dto.CreateReviewRequest;
import com.gramconnect.modules.review.dto.ReviewResponse;
import com.gramconnect.modules.review.entity.Review;
import com.gramconnect.modules.review.repository.ReviewRepository;
import com.gramconnect.modules.service.dto.*;
import com.gramconnect.modules.service.entity.*;
import com.gramconnect.modules.service.repository.*;
import com.gramconnect.modules.user.entity.Role;
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
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceMarketplaceService {

    private final ServiceCategoryRepository categoryRepository;
    private final ServiceProviderProfileRepository providerRepository;
    private final ServiceOfferingRepository offeringRepository;
    private final ServiceBookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final UserSavedServiceRepository savedServiceRepository;
    private final UserRepository userRepository;

    // ------------------------------------------------------------------------
    // Categories
    // ------------------------------------------------------------------------
    @Transactional(readOnly = true)
    @Cacheable(value = "service_categories", key = "'all'")
    public List<ServiceCategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(ServiceCategoryResponse::fromEntity)
                .toList();
    }

    // ------------------------------------------------------------------------
    // Provider Profile Management
    // ------------------------------------------------------------------------
    @Transactional
    public ServiceProviderResponse createProviderProfile(UUID userId, CreateProviderProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (providerRepository.findByUserId(userId).isPresent()) {
            throw new ConflictException("Service provider profile already exists for this user");
        }

        user.setRole(Role.ROLE_SERVICE_PROVIDER);
        userRepository.save(user);

        ServiceProviderProfile profile = ServiceProviderProfile.builder()
                .user(user)
                .bio(request.getBio())
                .experienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0)
                .serviceRadiusKm(request.getServiceRadiusKm())
                .isAvailable(true)
                .verificationStatus("PENDING")
                .idProofUrl(request.getIdProofUrl())
                .skillCertificateUrl(request.getSkillCertificateUrl())
                .averageRating(BigDecimal.ZERO)
                .totalReviews(0)
                .totalCompletedJobs(0)
                .build();

        ServiceProviderProfile savedProfile = providerRepository.save(profile);
        log.info("Created service provider profile [ID: {}] for user: {}", savedProfile.getId(), userId);
        return ServiceProviderResponse.fromEntity(savedProfile);
    }

    @Transactional
    public ServiceProviderResponse updateProviderProfile(UUID userId, UpdateProviderProfileRequest request) {
        ServiceProviderProfile profile = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProviderProfile", "userId", userId));

        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (request.getServiceRadiusKm() != null) profile.setServiceRadiusKm(request.getServiceRadiusKm());
        if (request.getIsAvailable() != null) profile.setIsAvailable(request.getIsAvailable());
        if (request.getIdProofUrl() != null) profile.setIdProofUrl(request.getIdProofUrl());
        if (request.getSkillCertificateUrl() != null) profile.setSkillCertificateUrl(request.getSkillCertificateUrl());

        ServiceProviderProfile saved = providerRepository.save(profile);
        return ServiceProviderResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ServiceProviderResponse getProviderById(UUID providerId) {
        ServiceProviderProfile profile = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProviderProfile", "id", providerId));
        return ServiceProviderResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public ServiceProviderResponse getProviderByUserId(UUID userId) {
        ServiceProviderProfile profile = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProviderProfile", "userId", userId));
        return ServiceProviderResponse.fromEntity(profile);
    }

    @Transactional(readOnly = true)
    public PageResponse<ServiceProviderResponse> searchProviders(
            UUID villageId,
            UUID categoryId,
            BigDecimal minRating,
            Boolean isAvailable,
            Pageable pageable) {

        Page<ServiceProviderProfile> page = providerRepository.searchProviders(
                villageId,
                categoryId,
                minRating,
                isAvailable,
                "VERIFIED",
                pageable);

        return PageResponse.from(page.map(ServiceProviderResponse::fromEntity));
    }

    // ------------------------------------------------------------------------
    // Service Offerings
    // ------------------------------------------------------------------------
    @Transactional
    public ServiceOfferingResponse addOffering(UUID userId, CreateOfferingRequest request) {
        ServiceProviderProfile provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProviderProfile", "userId", userId));

        ServiceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceCategory", "id", request.getCategoryId()));

        if (offeringRepository.findByProviderIdAndCategoryId(provider.getId(), category.getId()).isPresent()) {
            throw new ConflictException("Offering already exists for category: " + category.getDisplayName());
        }

        ServiceOffering offering = ServiceOffering.builder()
                .provider(provider)
                .category(category)
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .priceUnit(request.getPriceUnit())
                .isActive(true)
                .build();

        ServiceOffering saved = offeringRepository.save(offering);
        return ServiceOfferingResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ServiceOfferingResponse> getOfferingsByProvider(UUID providerId) {
        return offeringRepository.findByProviderIdAndIsActiveTrue(providerId).stream()
                .map(ServiceOfferingResponse::fromEntity)
                .toList();
    }

    // ------------------------------------------------------------------------
    // Bookings Lifecycle
    // ------------------------------------------------------------------------
    @Transactional
    public ServiceBookingResponse createBooking(UUID villagerId, CreateServiceBookingRequest request) {
        User villager = userRepository.findById(villagerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", villagerId));

        ServiceOffering offering = offeringRepository.findById(request.getOfferingId())
                .orElseThrow(() -> new ResourceNotFoundException("ServiceOffering", "id", request.getOfferingId()));

        ServiceProviderProfile provider = offering.getProvider();

        if (provider.getUser().getId().equals(villagerId)) {
            throw new BadRequestException("You cannot book your own service offering");
        }

        if (!Boolean.TRUE.equals(provider.getIsAvailable())) {
            throw new BadRequestException("This service provider is currently marked as unavailable");
        }

        ServiceBooking booking = ServiceBooking.builder()
                .villager(villager)
                .offering(offering)
                .provider(provider)
                .status(BookingStatus.REQUESTED)
                .scheduledDate(request.getScheduledDate())
                .scheduledTimeSlot(request.getScheduledTimeSlot())
                .addressNotes(request.getAddressNotes())
                .problemDescription(request.getProblemDescription())
                .quotedPrice(offering.getBasePrice())
                .build();

        ServiceBooking saved = bookingRepository.save(booking);
        log.info("Created service booking [ID: {}] for villager: {} with provider: {}", saved.getId(), villagerId, provider.getId());
        return ServiceBookingResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ServiceBookingResponse> getVillagerBookings(UUID villagerId, Pageable pageable) {
        Page<ServiceBooking> page = bookingRepository.findByVillagerId(villagerId, pageable);
        return PageResponse.from(page.map(ServiceBookingResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public PageResponse<ServiceBookingResponse> getProviderBookings(UUID userId, Pageable pageable) {
        ServiceProviderProfile provider = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProviderProfile", "userId", userId));

        Page<ServiceBooking> page = bookingRepository.findByProviderId(provider.getId(), pageable);
        return PageResponse.from(page.map(ServiceBookingResponse::fromEntity));
    }

    @Transactional
    public ServiceBookingResponse updateBookingStatus(UUID bookingId, UUID actorUserId, UpdateBookingStatusRequest request) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceBooking", "id", bookingId));

        boolean isVillager = booking.getVillager().getId().equals(actorUserId);
        boolean isProvider = booking.getProvider().getUser().getId().equals(actorUserId);

        if (!isVillager && !isProvider) {
            throw new ForbiddenException("You do not have permission to modify this booking");
        }

        BookingStatus currentStatus = booking.getStatus();
        BookingStatus targetStatus = request.getStatus();

        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new BadRequestException(String.format("Cannot transition booking status from %s to %s", currentStatus, targetStatus));
        }

        // Apply state changes and record lifecycle timestamps
        booking.setStatus(targetStatus);
        Instant now = Instant.now();

        switch (targetStatus) {
            case ACCEPTED -> {
                booking.setAcceptedAt(now);
                if (request.getQuotedPrice() != null) {
                    booking.setQuotedPrice(request.getQuotedPrice());
                }
            }
            case IN_PROGRESS -> booking.setStartedAt(now);
            case COMPLETED -> {
                booking.setCompletedAt(now);
                if (request.getFinalPrice() != null) {
                    booking.setFinalPrice(request.getFinalPrice());
                }
                // Increment provider completed jobs counter
                ServiceProviderProfile provider = booking.getProvider();
                provider.setTotalCompletedJobs(provider.getTotalCompletedJobs() + 1);
                providerRepository.save(provider);
            }
            case CANCELLED -> {
                booking.setCancelledAt(now);
                booking.setCancellationReason(request.getCancellationReason());
            }
            case DECLINED -> booking.setCancellationReason(request.getCancellationReason());
            default -> {}
        }

        ServiceBooking saved = bookingRepository.save(booking);
        log.info("Booking [ID: {}] transitioned from {} to {}", bookingId, currentStatus, targetStatus);
        return ServiceBookingResponse.fromEntity(saved);
    }

    // ------------------------------------------------------------------------
    // Verified Reviews (1:1 with Completed Booking & O(1) Aggregate Update)
    // ------------------------------------------------------------------------
    @Transactional
    public ReviewResponse submitReview(UUID bookingId, UUID reviewerId, CreateReviewRequest request) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceBooking", "id", bookingId));

        if (!booking.getVillager().getId().equals(reviewerId)) {
            throw new ForbiddenException("Only the villager who placed this booking can submit a review");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("Reviews can only be submitted for COMPLETED service bookings");
        }

        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new ConflictException("A review has already been submitted for this booking");
        }

        ServiceProviderProfile provider = booking.getProvider();

        Review review = Review.builder()
                .booking(booking)
                .reviewer(booking.getVillager())
                .provider(provider)
                .rating(request.getRating())
                .punctualityRating(request.getPunctualityRating())
                .qualityRating(request.getQualityRating())
                .pricingRating(request.getPricingRating())
                .behaviorRating(request.getBehaviorRating())
                .reviewTitle(request.getReviewTitle())
                .reviewComment(request.getReviewComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // Transactional Aggregate Rating Recalculation:
        // new_avg = (old_avg * old_count + new_rating) / (old_count + 1)
        int oldCount = provider.getTotalReviews();
        BigDecimal oldAvg = provider.getAverageRating();
        BigDecimal newRating = BigDecimal.valueOf(request.getRating());

        BigDecimal sumOfRatings = oldAvg.multiply(BigDecimal.valueOf(oldCount)).add(newRating);
        int newCount = oldCount + 1;
        BigDecimal newAvg = sumOfRatings.divide(BigDecimal.valueOf(newCount), 2, RoundingMode.HALF_UP);

        provider.setTotalReviews(newCount);
        provider.setAverageRating(newAvg);
        providerRepository.save(provider);

        log.info("Saved review [ID: {}] for provider [ID: {}]. Updated rating to: {} (Total: {})",
                savedReview.getId(), provider.getId(), newAvg, newCount);

        return ReviewResponse.fromEntity(savedReview);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getProviderReviews(UUID providerId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProviderId(providerId, pageable);
        return PageResponse.from(page.map(ReviewResponse::fromEntity));
    }

    // ------------------------------------------------------------------------
    // Favorites & Bookmarks
    // ------------------------------------------------------------------------
    @Transactional
    public void toggleFavorite(UUID userId, UUID providerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ServiceProviderProfile provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceProviderProfile", "id", providerId));

        if (savedServiceRepository.existsByUserIdAndProviderId(userId, providerId)) {
            savedServiceRepository.deleteByUserIdAndProviderId(userId, providerId);
            log.info("Removed provider {} from favorites for user {}", providerId, userId);
        } else {
            UserSavedService savedService = UserSavedService.builder()
                    .user(user)
                    .provider(provider)
                    .build();
            savedServiceRepository.save(savedService);
            log.info("Added provider {} to favorites for user {}", providerId, userId);
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<ServiceProviderResponse> getSavedProviders(UUID userId, Pageable pageable) {
        Page<UserSavedService> page = savedServiceRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(s -> ServiceProviderResponse.fromEntity(s.getProvider())));
    }
}

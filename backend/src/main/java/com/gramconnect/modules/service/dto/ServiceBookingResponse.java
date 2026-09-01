package com.gramconnect.modules.service.dto;

import com.gramconnect.modules.service.entity.BookingStatus;
import com.gramconnect.modules.service.entity.ServiceBooking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBookingResponse {

    private UUID id;
    private UUID villagerId;
    private String villagerName;
    private String villagerPhone;
    private UUID providerId;
    private String providerName;
    private String providerPhone;
    private UUID offeringId;
    private String categoryDisplayName;
    private BookingStatus status;
    private LocalDate scheduledDate;
    private String scheduledTimeSlot;
    private String addressNotes;
    private String problemDescription;
    private BigDecimal quotedPrice;
    private BigDecimal finalPrice;
    private Instant acceptedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;
    private String cancellationReason;
    private Instant createdAt;

    public static ServiceBookingResponse fromEntity(ServiceBooking booking) {
        return ServiceBookingResponse.builder()
                .id(booking.getId())
                .villagerId(booking.getVillager().getId())
                .villagerName(booking.getVillager().getFullName())
                .villagerPhone(booking.getVillager().getPhoneNumber())
                .providerId(booking.getProvider().getId())
                .providerName(booking.getProvider().getUser().getFullName())
                .providerPhone(booking.getProvider().getUser().getPhoneNumber())
                .offeringId(booking.getOffering().getId())
                .categoryDisplayName(booking.getOffering().getCategory().getDisplayName())
                .status(booking.getStatus())
                .scheduledDate(booking.getScheduledDate())
                .scheduledTimeSlot(booking.getScheduledTimeSlot())
                .addressNotes(booking.getAddressNotes())
                .problemDescription(booking.getProblemDescription())
                .quotedPrice(booking.getQuotedPrice())
                .finalPrice(booking.getFinalPrice())
                .acceptedAt(booking.getAcceptedAt())
                .startedAt(booking.getStartedAt())
                .completedAt(booking.getCompletedAt())
                .cancelledAt(booking.getCancelledAt())
                .cancellationReason(booking.getCancellationReason())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}

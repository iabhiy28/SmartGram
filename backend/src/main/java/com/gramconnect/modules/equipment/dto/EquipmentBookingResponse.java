package com.gramconnect.modules.equipment.dto;

import com.gramconnect.modules.equipment.entity.EquipmentBooking;
import com.gramconnect.modules.equipment.entity.EquipmentBookingStatus;
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
public class EquipmentBookingResponse {

    private UUID id;
    private UUID equipmentId;
    private String equipmentTitle;
    private UUID ownerId;
    private String ownerName;
    private UUID renterId;
    private String renterName;
    private String renterPhone;
    private LocalDate startDate;
    private LocalDate endDate;
    private String rateType;
    private BigDecimal rateAmount;
    private Integer totalDays;
    private BigDecimal totalAmount;
    private EquipmentBookingStatus status;
    private String renterNotes;
    private String ownerNotes;
    private String cancellationReason;
    private Instant confirmedAt;
    private Instant cancelledAt;
    private Instant completedAt;
    private Instant createdAt;

    public static EquipmentBookingResponse fromEntity(EquipmentBooking entity) {
        return EquipmentBookingResponse.builder()
                .id(entity.getId())
                .equipmentId(entity.getEquipment().getId())
                .equipmentTitle(entity.getEquipment().getTitle())
                .ownerId(entity.getEquipment().getOwner().getId())
                .ownerName(entity.getEquipment().getOwner().getFullName())
                .renterId(entity.getRenter().getId())
                .renterName(entity.getRenter().getFullName())
                .renterPhone(entity.getRenter().getPhoneNumber())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .rateType(entity.getRateType())
                .rateAmount(entity.getRateAmount())
                .totalDays(entity.getTotalDays())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .renterNotes(entity.getRenterNotes())
                .ownerNotes(entity.getOwnerNotes())
                .cancellationReason(entity.getCancellationReason())
                .confirmedAt(entity.getConfirmedAt())
                .cancelledAt(entity.getCancelledAt())
                .completedAt(entity.getCompletedAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

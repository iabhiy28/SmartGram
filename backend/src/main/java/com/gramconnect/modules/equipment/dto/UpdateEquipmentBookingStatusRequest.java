package com.gramconnect.modules.equipment.dto;

import com.gramconnect.modules.equipment.entity.EquipmentBookingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEquipmentBookingStatusRequest {

    @NotNull(message = "Target status is required")
    private EquipmentBookingStatus status;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String ownerNotes;

    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    private String cancellationReason;
}

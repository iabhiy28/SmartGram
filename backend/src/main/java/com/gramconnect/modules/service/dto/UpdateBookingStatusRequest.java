package com.gramconnect.modules.service.dto;

import com.gramconnect.modules.service.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingStatusRequest {

    @NotNull(message = "New status is required")
    private BookingStatus status;

    private BigDecimal quotedPrice;
    private BigDecimal finalPrice;
    private String cancellationReason;
}

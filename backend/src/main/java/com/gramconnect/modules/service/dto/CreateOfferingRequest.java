package com.gramconnect.modules.service.dto;

import com.gramconnect.modules.service.entity.PriceUnit;
import com.gramconnect.modules.service.entity.ServiceOffering;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOfferingRequest {

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private String description;

    @DecimalMin(value = "0.0", message = "Base price cannot be negative")
    private BigDecimal basePrice;

    @NotNull(message = "Price unit is required")
    private PriceUnit priceUnit;
}

package com.gramconnect.modules.service.dto;

import com.gramconnect.modules.service.entity.PriceUnit;
import com.gramconnect.modules.service.entity.ServiceOffering;
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
public class ServiceOfferingResponse {

    private UUID id;
    private UUID providerId;
    private UUID categoryId;
    private String categoryName;
    private String categoryDisplayName;
    private String description;
    private BigDecimal basePrice;
    private PriceUnit priceUnit;
    private Boolean isActive;

    public static ServiceOfferingResponse fromEntity(ServiceOffering offering) {
        return ServiceOfferingResponse.builder()
                .id(offering.getId())
                .providerId(offering.getProvider().getId())
                .categoryId(offering.getCategory().getId())
                .categoryName(offering.getCategory().getName())
                .categoryDisplayName(offering.getCategory().getDisplayName())
                .description(offering.getDescription())
                .basePrice(offering.getBasePrice())
                .priceUnit(offering.getPriceUnit())
                .isActive(offering.getIsActive())
                .build();
    }
}

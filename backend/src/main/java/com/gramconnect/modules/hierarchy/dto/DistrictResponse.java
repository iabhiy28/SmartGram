package com.gramconnect.modules.hierarchy.dto;

import com.gramconnect.modules.hierarchy.entity.District;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictResponse {

    private UUID id;
    private UUID stateId;
    private String name;
    private String code;

    public static DistrictResponse fromEntity(District district) {
        return DistrictResponse.builder()
                .id(district.getId())
                .stateId(district.getState().getId())
                .name(district.getName())
                .code(district.getCode())
                .build();
    }
}

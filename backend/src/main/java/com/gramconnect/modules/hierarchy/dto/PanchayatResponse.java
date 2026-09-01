package com.gramconnect.modules.hierarchy.dto;

import com.gramconnect.modules.hierarchy.entity.Panchayat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PanchayatResponse {

    private UUID id;
    private UUID districtId;
    private String name;
    private String officeAddress;
    private String contactPhone;

    public static PanchayatResponse fromEntity(Panchayat panchayat) {
        return PanchayatResponse.builder()
                .id(panchayat.getId())
                .districtId(panchayat.getDistrict().getId())
                .name(panchayat.getName())
                .officeAddress(panchayat.getOfficeAddress())
                .contactPhone(panchayat.getContactPhone())
                .build();
    }
}

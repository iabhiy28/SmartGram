package com.gramconnect.modules.announcement.dto;

import com.gramconnect.modules.announcement.entity.EmergencyContact;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactResponse {
    private UUID id;
    private UUID villageId;
    private String villageName;
    private String name;
    private String designation;
    private String phoneNumber;
    private String alternatePhone;
    private String serviceType;

    public static EmergencyContactResponse fromEntity(EmergencyContact entity) {
        EmergencyContactResponseBuilder builder = EmergencyContactResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .designation(entity.getDesignation())
                .phoneNumber(entity.getPhoneNumber())
                .alternatePhone(entity.getAlternatePhone())
                .serviceType(entity.getServiceType());

        if (entity.getVillage() != null) {
            builder.villageId(entity.getVillage().getId())
                   .villageName(entity.getVillage().getName());
        }
        return builder.build();
    }
}

package com.gramconnect.modules.hierarchy.dto;

import com.gramconnect.modules.hierarchy.entity.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateResponse {

    private UUID id;
    private String name;
    private String code;

    public static StateResponse fromEntity(State state) {
        return StateResponse.builder()
                .id(state.getId())
                .name(state.getName())
                .code(state.getCode())
                .build();
    }
}

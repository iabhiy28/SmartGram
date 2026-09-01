package com.gramconnect.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Standardized Error body included inside ApiResponse.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String code;
    private String details;
    private List<ValidationErrorDetail> validationErrors;

    public static ErrorResponse of(String code, String details) {
        return ErrorResponse.builder()
                .code(code)
                .details(details)
                .build();
    }

    public static ErrorResponse validation(List<ValidationErrorDetail> errors) {
        return ErrorResponse.builder()
                .code("VALIDATION_FAILED")
                .details("Input validation constraints were violated")
                .validationErrors(errors)
                .build();
    }
}

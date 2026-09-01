package com.gramconnect.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates specific field-level validation errors (e.g. from JSR-380 @NotNull, @Size, etc.).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorDetail {

    private String field;
    private Object rejectedValue;
    private String message;
}

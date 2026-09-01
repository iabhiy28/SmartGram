package com.gramconnect.common.exception;

import com.gramconnect.common.dto.ApiResponse;
import com.gramconnect.common.dto.ErrorResponse;
import com.gramconnect.common.dto.ValidationErrorDetail;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized Global Exception Handler for all REST controllers.
 * Ensures consistent JSON error envelope structure across the entire platform.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom business and domain exceptions derived from AppException.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        log.warn("Domain Exception caught [Code: {}]: {}", ex.getErrorCode(), ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(ex.getMessage(), errorResponse));
    }

    /**
     * Handles JSR-380 validation failures on @Valid request bodies.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<ValidationErrorDetail> validationErrors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(ValidationErrorDetail.builder()
                    .field(fieldError.getField())
                    .rejectedValue(fieldError.getRejectedValue())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }

        log.warn("Validation failed for {} fields: {}", validationErrors.size(), validationErrors);
        ErrorResponse errorResponse = ErrorResponse.validation(validationErrors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed on request payload", errorResponse));
    }

    /**
     * Handles query/path parameter constraint violations.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationErrorDetail> validationErrors = ex.getConstraintViolations().stream()
                .map(v -> ValidationErrorDetail.builder()
                        .field(v.getPropertyPath().toString())
                        .rejectedValue(v.getInvalidValue())
                        .message(v.getMessage())
                        .build())
                .toList();

        ErrorResponse errorResponse = ErrorResponse.validation(validationErrors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Constraint validation failed", errorResponse));
    }

    /**
     * Handles database concurrency and pessimistic locking collisions (e.g. double bookings).
     */
    @ExceptionHandler({PessimisticLockingFailureException.class, CannotAcquireLockException.class})
    public ResponseEntity<ApiResponse<Void>> handleConcurrencyConflict(Exception ex) {
        log.warn("Concurrency conflict detected: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
                "CONCURRENCY_CONFLICT",
                "The requested resource is currently being modified by another operation. Please retry."
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Concurrent update conflict", errorResponse));
    }

    /**
     * Handles database unique constraint violations (e.g. duplicate phone, duplicate application).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
                "DATA_INTEGRITY_VIOLATION",
                "A database constraint was violated. A record with duplicate unique fields may already exist."
        );
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Data conflict", errorResponse));
    }

    /**
     * Handles Spring Security Access Denied (@PreAuthorize failures).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
                "FORBIDDEN_ACCESS",
                "You do not possess the required role or ownership permissions to perform this action."
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access forbidden", errorResponse));
    }

    /**
     * Handles Spring Security Authentication failures.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
                "AUTHENTICATION_REQUIRED",
                "Invalid authentication credentials or expired token."
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication required", errorResponse));
    }

    /**
     * Handles oversized file upload attempts (> 5MB).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        log.warn("File upload limit exceeded: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(
                "FILE_TOO_LARGE",
                "The uploaded file exceeds the maximum allowed size limit of 5MB."
        );
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.error("File upload size exceeded", errorResponse));
    }

    /**
     * Fallback catch-all for any unexpected uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled internal server error occurred: ", ex);
        ErrorResponse errorResponse = ErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "An unexpected internal error occurred. Please contact system support."
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", errorResponse));
    }
}

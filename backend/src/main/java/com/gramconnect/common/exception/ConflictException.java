package com.gramconnect.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown on concurrency conflicts, double bookings, duplicate applications, or duplicate phone numbers.
 * Translates to HTTP 409 Conflict.
 */
public class ConflictException extends AppException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT, "CONFLICT_ERROR");
    }

    public ConflictException(String message, String errorCode) {
        super(message, HttpStatus.CONFLICT, errorCode);
    }
}

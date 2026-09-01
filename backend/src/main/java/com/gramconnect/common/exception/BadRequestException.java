package com.gramconnect.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an invalid operation or client input violates business rules.
 * Translates to HTTP 400 Bad Request.
 */
public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

    public BadRequestException(String message, String errorCode) {
        super(message, HttpStatus.BAD_REQUEST, errorCode);
    }
}

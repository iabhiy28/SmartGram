package com.gramconnect.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an authenticated user attempts to mutate resources they do not own (IDOR defense).
 * Translates to HTTP 403 Forbidden.
 */
public class ForbiddenException extends AppException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN_ACCESS");
    }
}

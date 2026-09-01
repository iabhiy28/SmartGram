package com.gramconnect.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when credentials fail or JWT is invalid/expired.
 * Translates to HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}

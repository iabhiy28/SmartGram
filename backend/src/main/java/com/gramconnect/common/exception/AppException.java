package com.gramconnect.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base root exception for all custom business and domain exceptions in GramConnect.
 */
@Getter
public abstract class AppException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public AppException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public AppException(String message, Throwable cause, HttpStatus status, String errorCode) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
    }
}

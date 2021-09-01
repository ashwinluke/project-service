package com.marketlogic.app.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "Project not found"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid input"),
    CONFLICTS(HttpStatus.CONFLICT, "Invalid input");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getMessage() {
        return this.message;
    }
}

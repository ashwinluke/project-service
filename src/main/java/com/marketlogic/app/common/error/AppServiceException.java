package com.marketlogic.app.common.error;

public class AppServiceException extends RuntimeException {

    private ErrorCode errorCode;

    public AppServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

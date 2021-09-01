package com.marketlogic.app.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {
    private String messageKey;
    private String message;

    public ErrorMessage(ErrorCode errorCode) {
        this.messageKey = errorCode.name();
        this.message = errorCode.getMessage();
    }
}

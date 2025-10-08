package com.aipiabackend.support.model.exception;

import com.aipiabackend.support.model.ErrorCodeMessage;

public class AipiaException extends RuntimeException {

    private final ErrorCodeMessage errorCodeMessage;
    private final String detailMessage;

    public AipiaException(ErrorCodeMessage errorCodeMessage, String detailMessage) {
        super(fullMessage(errorCodeMessage, detailMessage));
        this.errorCodeMessage = errorCodeMessage;
        this.detailMessage = detailMessage;
    }

    public AipiaException(ErrorCodeMessage errorCodeMessage) {
        super(fullMessage(errorCodeMessage, null));
        this.errorCodeMessage = errorCodeMessage;
        this.detailMessage = null;
    }

    private static String fullMessage(ErrorCodeMessage errorCodeMessage, String detailMessage) {
        if (detailMessage != null) {
            return "[%s] %s - %s".formatted(errorCodeMessage.code(), errorCodeMessage.message(), detailMessage);
        } else {
            return "[%s] %s".formatted(errorCodeMessage.code(), errorCodeMessage.message());
        }
    }
}

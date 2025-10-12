package com.aipiabackend.support.model.exception;

import com.aipiabackend.support.model.ErrorCodeMessage;
import lombok.Getter;

@Getter
public class AipiaException extends RuntimeException {

    private final ErrorCodeMessage errorCodeMessage;
    private final String detailMessage;

    // todo: AllArgs 하나만 남겨두고 정적 생성자 메서드로 리팩터링
    public AipiaException(ErrorCodeMessage errorCodeMessage, String detailMessage, Throwable rootCause) {
        super(fullMessage(errorCodeMessage, detailMessage), rootCause);
        this.errorCodeMessage = errorCodeMessage;
        this.detailMessage = detailMessage;
    }

    public AipiaException(ErrorCodeMessage errorCodeMessage, Throwable rootCause) {
        super(fullMessage(errorCodeMessage, null), rootCause);
        this.errorCodeMessage = errorCodeMessage;
        this.detailMessage = null;
    }

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

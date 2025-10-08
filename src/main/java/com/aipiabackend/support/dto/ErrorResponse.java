package com.aipiabackend.support.dto;

import com.aipiabackend.support.model.ErrorCodeMessage;

public record ErrorResponse(
    String code,
    String message,
    String detailMessage
) {
    public static ErrorResponse of(ErrorCodeMessage errorCodeMessage) {
        return new ErrorResponse(errorCodeMessage.code(), errorCodeMessage.message(), null);
    }

    public static ErrorResponse ofDetail(ErrorCodeMessage errorCodeMessage, String detailMessage) {
        return new ErrorResponse(errorCodeMessage.code(), errorCodeMessage.message(), detailMessage);
    }
}
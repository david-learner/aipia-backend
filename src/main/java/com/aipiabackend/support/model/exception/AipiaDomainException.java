package com.aipiabackend.support.model.exception;

import com.aipiabackend.support.model.ErrorCodeMessage;
import lombok.Getter;

@Getter
public class AipiaDomainException extends AipiaException {

    public AipiaDomainException(ErrorCodeMessage errorCodeMessage, String detailMessage) {
        super(errorCodeMessage, detailMessage);
    }

    public AipiaDomainException(ErrorCodeMessage errorCodeMessage) {
        super(errorCodeMessage);
    }
}

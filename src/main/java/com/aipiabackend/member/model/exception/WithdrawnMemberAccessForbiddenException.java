package com.aipiabackend.member.model.exception;

import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;

public class WithdrawnMemberAccessForbiddenException extends AipiaException {
    public WithdrawnMemberAccessForbiddenException(ErrorCodeMessage errorCodeMessage, String detailMessage) {
        super(errorCodeMessage, detailMessage);
    }
}

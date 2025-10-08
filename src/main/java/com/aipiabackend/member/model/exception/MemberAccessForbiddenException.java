package com.aipiabackend.member.model.exception;

import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;

public class MemberAccessForbiddenException extends AipiaException {
    public MemberAccessForbiddenException(ErrorCodeMessage errorCodeMessage, String detailMessage) {
        super(errorCodeMessage, detailMessage);
    }
}

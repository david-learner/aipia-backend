package com.aipiabackend.member.model.exception;

import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;

public class DuplicatedPhoneExistenceException extends AipiaException {
    public DuplicatedPhoneExistenceException(ErrorCodeMessage errorCodeMessage,
                                             String detailMessage) {
        super(errorCodeMessage, detailMessage);
    }

    public DuplicatedPhoneExistenceException(ErrorCodeMessage errorCodeMessage) {
        super(errorCodeMessage);
    }
}
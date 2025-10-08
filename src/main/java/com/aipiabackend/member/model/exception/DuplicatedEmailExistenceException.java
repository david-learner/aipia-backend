package com.aipiabackend.member.model.exception;

import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;

public class DuplicatedEmailExistenceException extends AipiaException {
    public DuplicatedEmailExistenceException(ErrorCodeMessage errorCodeMessage,
                                             String detailMessage) {
        super(errorCodeMessage, detailMessage);
    }

    public DuplicatedEmailExistenceException(ErrorCodeMessage errorCodeMessage) {
        super(errorCodeMessage);
    }
}
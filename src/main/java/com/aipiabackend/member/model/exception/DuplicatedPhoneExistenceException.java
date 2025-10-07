package com.aipiabackend.member.model.exception;

public class DuplicatedPhoneExistenceException extends RuntimeException {
    public DuplicatedPhoneExistenceException(String message) {
        super(message);
    }
}
package com.aipiabackend.member.model.exception;

public class DuplicatedEmailExistenceException extends RuntimeException {
    public DuplicatedEmailExistenceException(String message) {
        super(message);
    }
}
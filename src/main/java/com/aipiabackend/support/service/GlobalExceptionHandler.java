package com.aipiabackend.support.service;

import com.aipiabackend.member.model.exception.DuplicatedEmailExistenceException;
import com.aipiabackend.member.model.exception.DuplicatedPhoneExistenceException;
import com.aipiabackend.support.dto.ErrorResponse;
import com.aipiabackend.support.model.ErrorCodeMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedPhoneExistenceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedPhoneExistence(DuplicatedPhoneExistenceException e) {
        ErrorCodeMessage errorCodeMessage = ErrorCodeMessage.DUPLICATED_PHONE_EXISTENCE;
        ErrorResponse errorResponse = new ErrorResponse(errorCodeMessage.code(), errorCodeMessage.message());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(DuplicatedEmailExistenceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedEmailExistence(DuplicatedEmailExistenceException e) {
        ErrorCodeMessage errorCodeMessage = ErrorCodeMessage.DUPLICATED_EMAIL_EXISTENCE;
        ErrorResponse errorResponse = new ErrorResponse(errorCodeMessage.code(), errorCodeMessage.message());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
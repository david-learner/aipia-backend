package com.aipiabackend.support.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.UNKNOWN;

import com.aipiabackend.support.dto.ErrorResponse;
import com.aipiabackend.support.model.exception.AipiaDomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AipiaDomainException.class)
    public ResponseEntity<ErrorResponse> handleAipiaDomainException(AipiaDomainException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of(exception.getErrorCodeMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.ofDetail(UNKNOWN, exception.getBindingResult().getFieldError().getDefaultMessage()));
    }
}
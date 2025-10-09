package com.aipiabackend.support.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.UNKNOWN;

import com.aipiabackend.member.model.exception.DuplicatedEmailExistenceException;
import com.aipiabackend.member.model.exception.DuplicatedPhoneExistenceException;
import com.aipiabackend.member.model.exception.MemberAccessForbiddenException;
import com.aipiabackend.member.model.exception.WithdrawnMemberAccessForbiddenException;
import com.aipiabackend.support.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicatedPhoneExistenceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedPhoneExistence(DuplicatedPhoneExistenceException exception) {
        ErrorResponse errorResponse =
            ErrorResponse.ofDetail(exception.getErrorCodeMessage(), exception.getDetailMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(DuplicatedEmailExistenceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedEmailExistence(DuplicatedEmailExistenceException exception) {
        ErrorResponse errorResponse =
            ErrorResponse.ofDetail(exception.getErrorCodeMessage(), exception.getDetailMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MemberAccessForbiddenException.class)
    public ResponseEntity<Void> handleMemberAccessForbidden(MemberAccessForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(WithdrawnMemberAccessForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleWithdrawnMemberAccessForbidden(
        WithdrawnMemberAccessForbiddenException exception
    ) {
        ErrorResponse errorResponse = ErrorResponse.of(exception.getErrorCodeMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.ofDetail(UNKNOWN, exception.getBindingResult().getFieldError().getDefaultMessage()));
    }
}
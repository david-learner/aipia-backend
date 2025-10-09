package com.aipiabackend.support.model;

public enum ErrorCodeMessage {

    UNKNOWN("AIPIA-0000", "식별되지 않은 오류입니다."),
    DUPLICATED_PHONE_EXISTENCE("AIPIA-0001", "이미 존재하는 휴대폰 번호입니다."),
    DUPLICATED_EMAIL_EXISTENCE("AIPIA-0002", "이미 존재하는 이메일입니다."),
    INVALID_LOGIN_INPUT("AIPIA-0003", "로그인 정보가 올바르지 않습니다."),
    MEMBER_NOT_FOUND("AIPIA-0004", "회원을 찾을 수 없습니다."),
    MEMBER_ACCESS_FORBIDDEN("AIPIA-0005", "해당 회원 정보에 접근할 권한이 없습니다."),
    UNAUTHORIZED_USER("AIPIA-0006", "인증되지 않은 사용자입니다.");

    private String code;
    private String message;

    ErrorCodeMessage(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}

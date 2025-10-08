package com.aipiabackend.support.model;

public enum ErrorCodeMessage {

    UNKNOWN("AIPIA-0000", "식별되지 않은 오류입니다."),
    DUPLICATED_PHONE_EXISTENCE("AIPIA-0001", "이미 존재하는 휴대폰 번호입니다."),
    DUPLICATED_EMAIL_EXISTENCE("AIPIA-0002", "이미 존재하는 이메일입니다.");

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

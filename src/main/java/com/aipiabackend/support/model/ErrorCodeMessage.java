package com.aipiabackend.support.model;

public enum ErrorCodeMessage {

    UNKNOWN("AIPIA-0000", "식별되지 않은 오류입니다."),
    DUPLICATED_PHONE_EXISTENCE("AIPIA-0001", "이미 존재하는 휴대폰 번호입니다."),
    DUPLICATED_EMAIL_EXISTENCE("AIPIA-0002", "이미 존재하는 이메일입니다."),
    INVALID_LOGIN_INPUT("AIPIA-0003", "로그인 정보가 올바르지 않습니다."),
    MEMBER_NOT_FOUND("AIPIA-0004", "회원을 찾을 수 없습니다."),
    MEMBER_ACCESS_FORBIDDEN("AIPIA-0005", "해당 회원 정보에 접근할 권한이 없습니다."),
    UNAUTHORIZED_USER("AIPIA-0006", "인증되지 않은 사용자입니다."),
    ACCESS_DENIED("AIPIA-0007", "접근 권한이 없습니다."),
    WITHDRAWN_MEMBER_ACCESS_FORBIDDEN("AIPIA-0008", "탈퇴한 회원은 조회할 수 없습니다."),
    PRODUCT_NOT_FOUND("AIPIA-0009", "상품을 찾을 수 없습니다."),
    ORDER_LINE_AMOUNT_NOT_MATCHED("AIPIA-0010", "주문 라인의 주문 총액이 일치하지 않습니다."),
    NOT_ENOUGH_STOCK("AIPIA-0011", "재고가 부족합니다."),
    PAYMENT_SERVICE_PROVIDER_IS_NOT_WORKING("AIPIA-0012", "결제 서비스 제공자가 동작하지 않습니다."),
    FAILED_TO_PAY("AIPIA-0013", "결제에 실패하였습니다."),
    ORDER_CAN_BE_SUCCEEDED_FROM_PENDING("AIPIA-0014", "주문 대기 상태에서만 주문 성공으로 변경될 수 있습니다."),
    ORDER_CAN_BE_FAILED_FROM_PENDING("AIPIA-0014", "주문 대기 상태에서만 주문 실패으로 변경될 수 있습니다."),
    PAYMENT_NOT_FOUND("AIPIA-0015", "결제 정보를 찾을 수 없습니다."),
    ORDER_NOT_FOUND("AIPIA-0016", "주문을 찾을 수 없습니다."),
    ORDER_ACCESS_FORBIDDEN("AIPIA-0017", "해당 주문에 접근할 권한이 없습니다."),
    ;

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

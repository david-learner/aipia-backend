package com.aipiabackend.payment.model;

public enum PaymentStatus {
    PENDING,    // 대기: 결제 요청이 생성되었으나 처리되지 않은 상태
    SUCCEEDED,  // 성공: 결제가 정상적으로 완료된 상태
    FAILED,     // 실패: 결제 처리 중 오류가 발생하여 실패한 상태
    REFUNDED,   // 환불: 결제 성공 후 환불 처리된 상태
    CANCELED    // 취소: 사용자에 의해 결제가 취소된 상태
}

package com.aipiabackend.payment.client.dto;

public record PaymentResponse(
    boolean success,
    String transactionId,
    String message // 결제 실패시 오류 메시지
) {
    public static PaymentResponse success(String transactionId) {
        return new PaymentResponse(true, transactionId, "결제가 성공적으로 완료되었습니다.");
    }

    public static PaymentResponse failure(String message) {
        return new PaymentResponse(false, null, message);
    }
}

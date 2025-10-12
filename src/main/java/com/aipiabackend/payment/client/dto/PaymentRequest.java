package com.aipiabackend.payment.client.dto;

/**
 * 외부 결제 시스템에 결제 요청을 위한 DTO
 */
public record PaymentRequest(
    Long orderId,
    String cardNumber,
    String cardExpirationYearAndMonth,
    String cardIssuerCode,
    Long amount
) {
}

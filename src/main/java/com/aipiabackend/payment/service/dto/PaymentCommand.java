package com.aipiabackend.payment.service.dto;

public record PaymentCommand(
    Long orderId,
    String cardNumber,
    String cardExpirationYearAndMonth,
    String cardIssuerCode
) {
}

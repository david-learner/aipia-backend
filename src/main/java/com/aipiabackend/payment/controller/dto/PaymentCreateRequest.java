package com.aipiabackend.payment.controller.dto;

import com.aipiabackend.payment.service.dto.PaymentCommand;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
    @NotNull(message = "주문 ID는 필수입니다")
    Long orderId,

    @NotNull(message = "카드 번호는 필수입니다")
    String cardNumber,

    @NotNull(message = "카드 만료 연월은 필수입니다")
    String cardExpirationYearAndMonth,

    @NotNull(message = "카드 발급사 코드는 필수입니다")
    String cardIssuerCode
) {
    public PaymentCommand toPayCommand() {
        return new PaymentCommand(
            orderId,
            cardNumber,
            cardExpirationYearAndMonth,
            cardIssuerCode
        );
    }
}

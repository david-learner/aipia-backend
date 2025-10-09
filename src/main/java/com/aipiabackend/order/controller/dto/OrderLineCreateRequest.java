package com.aipiabackend.order.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record OrderLineCreateRequest(
    @NotNull(message = "상품 ID는 필수입니다")
    Long productId,

    @NotNull(message = "상품 수량은 필수입니다")
    @Positive(message = "상품 수량은 1개 이상이어야 합니다")
    Integer productQuantity,

    @NotNull(message = "상품 가격은 필수입니다")
    Long productPrice,

    @NotNull(message = "금액은 필수입니다")
    Long amount
) {
}

package com.aipiabackend.order.service.dto;

import java.math.BigDecimal;

public record OrderLineCreateCommand(
    Long productId,
    Integer productQuantity,
    BigDecimal productPrice,
    BigDecimal amount
) {
}

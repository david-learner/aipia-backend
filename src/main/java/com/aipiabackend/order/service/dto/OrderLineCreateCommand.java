package com.aipiabackend.order.service.dto;

public record OrderLineCreateCommand(
    Long productId,
    Integer productQuantity,
    Long productPrice,
    Long amount
) {
}

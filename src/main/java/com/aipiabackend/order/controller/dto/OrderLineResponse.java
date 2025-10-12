package com.aipiabackend.order.controller.dto;

import com.aipiabackend.order.model.OrderLine;

/**
 * 주문 라인 응답 DTO
 */
public record OrderLineResponse(
    Long id,
    Long productId,
    Integer productQuantity,
    Long productPrice,
    Long amount
) {
    public static OrderLineResponse from(OrderLine orderLine) {
        return new OrderLineResponse(
            orderLine.getId(),
            orderLine.getProductId(),
            orderLine.getProductQuantity(),
            orderLine.getProductPrice(),
            orderLine.getAmount()
        );
    }
}

package com.aipiabackend.order.controller.dto;

import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 응답 DTO
 */
public record OrderResponse(
    Long id,
    Long memberId,
    OrderStatus status,
    List<OrderLineResponse> orderLines,
    Long amount,
    LocalDateTime orderedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getMemberId(),
            order.getStatus(),
            order.getOrderLines().stream()
                .map(OrderLineResponse::from)
                .toList(),
            order.getAmount(),
            order.getOrderedAt()
        );
    }
}

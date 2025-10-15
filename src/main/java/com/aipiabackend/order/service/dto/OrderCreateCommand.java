package com.aipiabackend.order.service.dto;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.model.OrderLine;
import java.util.List;

public record OrderCreateCommand(
    Long memberId,
    List<OrderLineCreateCommand> orderLines,
    Long amount
) {
    private List<OrderLine> toOrderLines() {
        return orderLines.stream().map(orderLine -> OrderLine.of(
            orderLine.productId(),
            orderLine.productQuantity(),
            orderLine.productPrice(),
            orderLine.amount())).toList();
    }

    public Order toOrder(Member member) {
        return Order.of(member, toOrderLines(), amount);
    }
}

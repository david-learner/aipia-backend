package com.aipiabackend.order.controller.dto;

import com.aipiabackend.order.service.dto.OrderCreateCommand;
import com.aipiabackend.order.service.dto.OrderLineCreateCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderCreateRequest(
    @NotNull(message = "회원 ID는 필수입니다")
    Long memberId,

    @NotEmpty(message = "주문 라인은 최소 1개 이상이어야 합니다")
    @Valid
    List<OrderLineCreateRequest> orderLines,

    @NotNull(message = "주문 금액은 필수입니다")
    Long amount
) {

    /**
     * OrderCreateRequest를 OrderCreateCommand로 변환한다
     */
    public OrderCreateCommand toOrderCreateCommand() {
        List<OrderLineCreateCommand> orderLineCreateCommands = orderLines.stream()
            .map(this::toOrderLineCommand)
            .toList();

        return new OrderCreateCommand(
            memberId,
            orderLineCreateCommands,
            amount
        );
    }

    /**
     * OrderLineCreateRequest를 OrderLineCreateCommand로 변환한다
     */
    private OrderLineCreateCommand toOrderLineCommand(OrderLineCreateRequest request) {
        return new OrderLineCreateCommand(
            request.productId(),
            request.productQuantity(),
            request.productPrice(),
            request.amount()
        );
    }
}

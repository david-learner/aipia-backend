package com.aipiabackend.order.model;

import static com.aipiabackend.support.model.ErrorCodeMessage.NOT_ENOUGH_STOCK;
import static com.aipiabackend.support.model.ErrorCodeMessage.ORDER_CAN_BE_SUCCEEDED_FROM_PENDING;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.aipiabackend.support.model.exception.AipiaException;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void 주문_대기_상태에서_주문_완료_처리가_가능하다() {
        List<OrderLine> orderLines = List.of(OrderLine.of(1L, 2, 1000L, 2000L));
        Order order = Order.of(1L, orderLines, 2000L);

        order.completePayment();

        assertEquals(OrderStatus.SUCCEEDED, order.getStatus());
    }

    @Test
    void 이미_완료된_주문을_완료처리_하면_예외를_발생시킨다() {
        List<OrderLine> orderLines = List.of(OrderLine.of(1L, 2, 1000L, 2000L));
        Order order = Order.of(1L, orderLines, 2000L);

        order.completePayment();
        assertThatThrownBy(() -> order.completePayment())
            .isInstanceOf(AipiaException.class)
            .hasMessageContaining(ORDER_CAN_BE_SUCCEEDED_FROM_PENDING.message());
    }
}
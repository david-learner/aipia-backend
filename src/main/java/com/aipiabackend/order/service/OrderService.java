package com.aipiabackend.order.service;

import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.repository.OrderRepository;
import com.aipiabackend.order.service.dto.OrderCreateCommand;
import com.aipiabackend.order.service.dto.OrderLineCreateCommand;
import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(OrderCreateCommand command) {
        List<OrderLineCreateCommand> orderLineCreateCommands = command.orderLines();

        // 가격 검증 및 재고 차감
        orderLineCreateCommands.forEach(orderLine -> {
            Product product = productRepository.findById(orderLine.productId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            // 요청된 주문 금액과 서버에서 계산한 주문 금액이 일치하는지 확인한다
            long actualOrderLineAmount = product.getPrice() * orderLine.productQuantity();
            if (orderLine.amount().compareTo(actualOrderLineAmount) != 0) {
                throw new IllegalArgumentException("주문 라인의 금액이 일치하지 않습니다.");
            }

            // 상품의 재고를 차감한다.
            product.decreaseStock(orderLine.productQuantity());
            productRepository.save(product);
        });

        Order order = command.toOrder();
        return orderRepository.save(order);
    }
}

package com.aipiabackend.order.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.*;

import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.repository.OrderRepository;
import com.aipiabackend.order.service.dto.OrderCreateCommand;
import com.aipiabackend.order.service.dto.OrderLineCreateCommand;
import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.service.ProductService;
import com.aipiabackend.support.model.exception.AipiaException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Transactional
    public Order create(OrderCreateCommand command) {
        List<OrderLineCreateCommand> orderLineCreateCommands = command.orderLines();

        // 가격 검증 및 재고 차감
        orderLineCreateCommands.forEach(orderLine -> {
            Product product = productService.findById(orderLine.productId());

            // 요청된 주문 금액과 서버에서 계산한 주문 금액이 일치하는지 확인한다
            long actualOrderLineAmount = product.getPrice() * orderLine.productQuantity();
            if (orderLine.amount().compareTo(actualOrderLineAmount) != 0) {
                throw new AipiaException(ORDER_LINE_AMOUNT_NOT_MATCHED);
            }

            // 상품의 재고를 차감한다.
            product.decreaseStock(orderLine.productQuantity());
            productService.save(product);
        });

        Order order = command.toOrder();
        return orderRepository.save(order);
    }
}

package com.aipiabackend.order.service;

import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.repository.OrderRepository;
import com.aipiabackend.order.service.dto.OrderCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderCreateCommand command) {
        Order order = command.toOrder();
        return orderRepository.save(order);
    }
}

package com.aipiabackend.order.controller;

import com.aipiabackend.order.controller.dto.OrderCreateRequest;
import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderRestController {

    private final OrderService orderService;

    /**
     * 주문을 생성한다
     */
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Order savedOrder = orderService.createOrder(request.toOrderCreateCommand());

        UriComponents orderUriComponents = UriComponentsBuilder
            .fromUriString("/api/orders/{orderId}")
            .buildAndExpand(savedOrder.getId());

        return ResponseEntity.created(orderUriComponents.toUri()).build();
    }
}

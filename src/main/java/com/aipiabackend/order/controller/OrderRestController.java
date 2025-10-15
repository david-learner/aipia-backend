package com.aipiabackend.order.controller;

import com.aipiabackend.auth.model.MemberPrincipal;
import com.aipiabackend.order.controller.dto.OrderCreateRequest;
import com.aipiabackend.order.controller.dto.OrderResponse;
import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "주문 생성", description = "주문을 생성한다")
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Order savedOrder = orderService.create(request.toOrderCreateCommand());

        UriComponents orderUriComponents = UriComponentsBuilder
            .fromUriString("/api/orders/{orderId}")
            .buildAndExpand(savedOrder.getId());

        return ResponseEntity.created(orderUriComponents.toUri()).build();
    }

    @Operation(summary = "주문 조회", description = "주문을 조회한다")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> retrieveOrder(
        @PathVariable Long orderId,
        @AuthenticationPrincipal MemberPrincipal principal
    ) {
        Order order = orderService.retrieveOrder(orderId, principal.getMemberId(), principal.getGrade());
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}

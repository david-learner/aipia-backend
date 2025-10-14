package com.aipiabackend.payment.controller;

import com.aipiabackend.payment.controller.dto.PaymentCreateRequest;
import com.aipiabackend.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/payments")
@RestController
public class PaymentRestController {

    private final PaymentService paymentService;

    /**
     * 주문을 결제한다
     */
    @PostMapping
    public ResponseEntity<Void> pay(@Valid @RequestBody PaymentCreateRequest request) {
        paymentService.pay(request.toPayCommand());
        return ResponseEntity.ok().build();
    }
}

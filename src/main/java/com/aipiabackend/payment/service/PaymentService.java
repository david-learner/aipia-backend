package com.aipiabackend.payment.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.FAILED_TO_PAY;

import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.service.OrderService;
import com.aipiabackend.payment.client.PaymentClient;
import com.aipiabackend.payment.client.dto.PaymentRequest;
import com.aipiabackend.payment.client.dto.PaymentResponse;
import com.aipiabackend.payment.service.dto.PaymentCommand;
import com.aipiabackend.support.model.exception.AipiaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRecordService paymentRecordService;
    private final OrderService orderService;
    private final PaymentClient paymentClient;

    @Transactional
    public void pay(PaymentCommand command) {
        Order order = orderService.findById(command.orderId());

        if (!order.isPending()) {
            throw new AipiaException(FAILED_TO_PAY,
                "주문 대기 중인 상태에서만 결제가 가능합니다. orderStatus='%s'".formatted(order.getStatus()));
        }

        var payment = paymentRecordService.createPayment(command.orderId(), order.getAmount());

        PaymentRequest paymentRequest = new PaymentRequest(
            command.orderId(),
            command.cardNumber(),
            command.cardExpirationYearAndMonth(),
            command.cardIssuerCode(),
            order.getAmount()
        );

        try {
            PaymentResponse paymentResponse = processPaymentWithRetry(paymentRequest);

            // 결제 성공
            if (paymentResponse.success()) {
                paymentRecordService.completePayment(payment.getId(), paymentResponse.transactionId());
                order.completePayment();
                orderService.save(order);
            } else {
                // 결제 실패
                throw new AipiaException(FAILED_TO_PAY, paymentResponse.message());
            }
        } catch (Exception exception) {
            // 결제 실패에 대한 후처리
            order.failPayment();
            orderService.save(order);
            paymentRecordService.failPayment(payment.getId());
            throw exception;
        }
    }

    /**
     * 결제를 재시도 한다. 최대 3회까지 재시도하며, 재시도 간 1초 대기
     */
    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    private PaymentResponse processPaymentWithRetry(PaymentRequest request) {
        try {
            PaymentResponse response = paymentClient.pay(request);

            if (response.success()) {
                log.debug("결제 성공: orderId={}", request.orderId());
            } else {
                log.warn("결제 실패: orderId={}, message={}", request.orderId(), response.message());
            }

            return response;
        } catch (Exception exception) {
            log.error("결제 처리 중 예외 발생: orderId={}", request.orderId(), exception);
            throw new AipiaException(FAILED_TO_PAY, exception);
        }
    }

    /**
     * 모든 재시도가 실패했을 때 호출되는 복구 메서드
     */
    @Recover
    private PaymentResponse recoverFromPaymentFailure(Exception e, PaymentRequest request) {
        log.error("결제 최종 실패 (모든 재시도 소진): orderId={}", request.orderId(), e);
        return PaymentResponse.failure("결제에 실패하였습니다.");
    }
}

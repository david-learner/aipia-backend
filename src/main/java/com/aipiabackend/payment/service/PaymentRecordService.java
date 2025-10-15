package com.aipiabackend.payment.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.PAYMENT_NOT_FOUND;

import com.aipiabackend.order.model.Order;
import com.aipiabackend.order.service.OrderService;
import com.aipiabackend.payment.model.Payment;
import com.aipiabackend.payment.repository.PaymentRepository;
import com.aipiabackend.support.model.exception.AipiaDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment 엔티티의 저장/업데이트를 담당하는 서비스
 */
@RequiredArgsConstructor
@Service
public class PaymentRecordService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Payment createPayment(Long orderId, Long amount) {
        Order order = orderService.findById(orderId);
        Payment payment = Payment.of(order, amount);
        return paymentRepository.save(payment);
    }

    /**
     * Payment를 성공 상태로 갱신한다
     *
     * @param paymentId     결제 ID
     * @param transactionId 외부 결제 시스템의 거래 ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completePayment(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new AipiaDomainException(PAYMENT_NOT_FOUND));
        payment.assignTransactionId(transactionId);
        payment.complete();
        paymentRepository.save(payment);
    }

    /**
     * Payment를 실패 상태로 갱신한다
     *
     * @param paymentId 결제 ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new AipiaDomainException(PAYMENT_NOT_FOUND));
        payment.fail();
        paymentRepository.save(payment);
    }
}

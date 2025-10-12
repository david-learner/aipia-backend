package com.aipiabackend.payment.client;

import com.aipiabackend.payment.client.dto.PaymentRequest;
import com.aipiabackend.payment.client.dto.PaymentResponse;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 테스트용 결제 클라이언트
 * - 외부 결제 시스템과의 실제 통신 없이 성공/실패 응답을 반환한다
 * - 카드번호 "0000000000000000"으로 요청 시 예외를 발생시켜 재시도 로직을 테스트할 수 있다
 */
@Component
public class MockPaymentClient implements PaymentClient {

    private static final String ALWAYS_FAIL_CARD_NUMBER = "0000000000000000";

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        // 테스트용: 특정 카드 번호로 요청 시 예외 발생 (재시도 로직 테스트용)
        if (ALWAYS_FAIL_CARD_NUMBER.equals(request.cardNumber())) {
            throw new RuntimeException("결제 시스템 일시적 오류 (테스트용)");
        }

        // 외부 결제 시스템에서 부여하는 거래 고유 번호를 UUID로 생성
        String transactionId = UUID.randomUUID().toString();
        return PaymentResponse.success(transactionId);
    }
}

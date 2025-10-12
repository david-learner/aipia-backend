package com.aipiabackend.payment.client;

import com.aipiabackend.payment.client.dto.PaymentRequest;
import com.aipiabackend.payment.client.dto.PaymentResponse;

/**
 * 외부 결제 시스템과 통신하는 클라이언트 인터페이스 각 결제 게이트웨이사의 구현체를 통해 실제 결제를 처리한다
 */
public interface PaymentClient {

    /**
     * 결제를 요청한다
     *
     * @param request 결제 요청 정보
     * @return 결제 응답 정보
     */
    PaymentResponse pay(PaymentRequest request);
}

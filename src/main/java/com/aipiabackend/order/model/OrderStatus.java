package com.aipiabackend.order.model;

public enum OrderStatus {
    PENDING,    // 주문 대기: 주문은 생성되었으나 결제가 완료되지 않은 상태
    CANCELED,   // 주문 취소: 사용자가 주문을 취소한 상태
    SUCCEEDED   // 주문 성공: 주문 생성 후 결제가 완료된 상태
}

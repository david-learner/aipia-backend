package com.aipiabackend.support.model;

/**
 * 주문과 주문자 정보
 *
 * @param orderLocation 주문의 Location 헤더 값
 * @param member        주문자
 */
public record OrderAndMember(
    String orderLocation,
    LoginedMember member
) {
}

package com.aipiabackend.support.model;

/**
 * 로그인 한 관리자
 */
public record LoginedAdmin(
    Long memberId,
    String accessToken
) {
}

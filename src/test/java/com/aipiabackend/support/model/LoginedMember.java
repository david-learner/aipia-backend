package com.aipiabackend.support.model;

/**
 * 로그인 한 회원
 */
public record LoginedMember(
    Long memberId,
    String accessToken
) {
}

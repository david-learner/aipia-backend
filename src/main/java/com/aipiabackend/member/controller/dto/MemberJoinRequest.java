package com.aipiabackend.member.controller.dto;

public record MemberJoinRequest(
    String name,
    String email,
    String password,
    String phone
) {
}

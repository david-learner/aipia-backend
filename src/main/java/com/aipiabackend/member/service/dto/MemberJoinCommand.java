package com.aipiabackend.member.service.dto;

public record MemberJoinCommand(
    String name,
    String email,
    String password,
    String phone
) {
}
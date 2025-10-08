package com.aipiabackend.member.controller.dto;

import com.aipiabackend.member.model.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberResponse(
    Long id,
    String name,
    String email,
    String phone,
    LocalDateTime joinedAt,
    LocalDateTime withdrawnAt
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getName(),
            member.getEmail(),
            member.getPhone(),
            member.getJoinedAt(),
            member.getWithdrawnAt()
        );
    }
}

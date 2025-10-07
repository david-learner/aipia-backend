package com.aipiabackend.member.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberJoinRequest(
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 10, message = "이름은 10자 이하여야 합니다")
    @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력 가능합니다")
    String name,
    String email,
    String password,
    String phone
) {
}

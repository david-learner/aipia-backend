package com.aipiabackend.auth.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "이메일은 필수입니다")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다")
    @Email(message = "올바른 이메일 형식이어야 합니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(max = 20, message = "비밀번호는 20자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "비밀번호는 영어 대/소문자, 숫자로만 구성되어야 합니다")
    String password
) {
}
package com.aipiabackend.auth.service;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        try {
            Member member = memberService.findByEmail(email);

            if (!passwordEncoder.matches(password, member.getPassword())) {
                throw new BadCredentialsException("Invalid email or password");
            }

            // JWT 토큰 생성 (현재는 간단한 구현)
            return generateAccessToken(member);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    // todo: JWT 라이브러리 추가 후 구현
    private String generateAccessToken(Member member) {
        return "jwt-token-for-member-" + member.getId();
    }
}
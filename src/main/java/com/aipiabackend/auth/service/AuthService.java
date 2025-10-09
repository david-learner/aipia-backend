package com.aipiabackend.auth.service;

import com.aipiabackend.auth.model.MemberPrincipal;
import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.model.MemberGrade;
import com.aipiabackend.member.service.MemberService;
import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증과 관련된 기능을 제공하는 서비스
 * <br> - 로그인
 * <br> - 스프링 시큐리티 인증 주체 생성
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        try {
            Member member = memberService.findByEmail(email);

            if (!passwordEncoder.matches(password, member.getPassword())) {
                throw new BadCredentialsException(ErrorCodeMessage.INVALID_LOGIN_INPUT.message());
            }

            return tokenService.generateAccessToken(member);
        } catch (AipiaException exception) {
            throw new BadCredentialsException(ErrorCodeMessage.INVALID_LOGIN_INPUT.message());
        }
    }

    /**
     * 회원 인증 주체를 생성한다.
     */
    public MemberPrincipal createMemberPrincipal(String token) {
        Long memberId = tokenService.extractMemberId(token);
        String gradeString = tokenService.extractGrade(token);
        MemberGrade grade = MemberGrade.valueOf(gradeString);
        MemberPrincipal principal = new MemberPrincipal(memberId, grade);
        return principal;
    }
}
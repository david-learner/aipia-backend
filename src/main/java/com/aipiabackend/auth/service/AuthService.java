package com.aipiabackend.auth.service;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.service.MemberService;
import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
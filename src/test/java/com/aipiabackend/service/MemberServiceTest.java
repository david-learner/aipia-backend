package com.aipiabackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import com.aipiabackend.member.service.MemberService;
import com.aipiabackend.member.service.dto.MemberJoinCommand;
import com.aipiabackend.member.model.exception.DuplicatedEmailExistenceException;
import com.aipiabackend.member.model.exception.DuplicatedPhoneExistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 유효한_입력이_주어지면_회원가입에_성공한다() {
        MemberJoinCommand command = new MemberJoinCommand(
            "홍길동",
            "hong@example.com",
            "password123",
            "010-1234-5678"
        );

        given(memberRepository.existsByEmail(command.email())).willReturn(false);
        given(memberRepository.existsByPhone(command.phone())).willReturn(false);
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> invocation.getArgument(0));

        Member result = memberService.join(command);

        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getEmail()).isEqualTo("hong@example.com");
        assertThat(result.getPhone()).isEqualTo("010-1234-5678");
        then(memberRepository).should().existsByEmail(command.email());
        then(memberRepository).should().existsByPhone(command.phone());
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    void 동일한_이메일이_존재하면_DuplicatedEmailExistenceException이_발생한다() {
        MemberJoinCommand command = new MemberJoinCommand(
            "홍길동",
            "hong@example.com",
            "password123",
            "010-1234-5678"
        );

        given(memberRepository.existsByEmail(command.email())).willReturn(true);

        assertThatThrownBy(() -> memberService.join(command))
            .isInstanceOf(DuplicatedEmailExistenceException.class);

        then(memberRepository).should().existsByEmail(command.email());
        then(memberRepository).should(never()).existsByPhone(any());
        then(memberRepository).should(never()).save(any(Member.class));
    }

    @Test
    void 동일한_휴대폰이_존재하면_DuplicatedPhoneExistenceException이_발생한다() {
        MemberJoinCommand command = new MemberJoinCommand(
            "홍길동",
            "hong@example.com",
            "password123",
            "010-1234-5678"
        );

        given(memberRepository.existsByEmail(command.email())).willReturn(false);
        given(memberRepository.existsByPhone(command.phone())).willReturn(true);

        assertThatThrownBy(() -> memberService.join(command))
            .isInstanceOf(DuplicatedPhoneExistenceException.class);

        then(memberRepository).should().existsByEmail(command.email());
        then(memberRepository).should().existsByPhone(command.phone());
        then(memberRepository).should(never()).save(any(Member.class));
    }
}

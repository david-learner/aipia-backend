package com.aipiabackend.member.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.DUPLICATED_EMAIL_EXISTENCE;
import static com.aipiabackend.support.model.ErrorCodeMessage.DUPLICATED_PHONE_EXISTENCE;
import static com.aipiabackend.support.model.ErrorCodeMessage.MEMBER_ACCESS_FORBIDDEN;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.model.exception.DuplicatedEmailExistenceException;
import com.aipiabackend.member.model.exception.DuplicatedPhoneExistenceException;
import com.aipiabackend.member.model.exception.MemberAccessForbiddenException;
import com.aipiabackend.member.repository.MemberRepository;
import com.aipiabackend.member.service.dto.MemberJoinCommand;
import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.exception.AipiaException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member join(MemberJoinCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new DuplicatedEmailExistenceException(
                DUPLICATED_EMAIL_EXISTENCE, "email='%s'".formatted(command.email()));
        }

        if (memberRepository.existsByPhone(command.phone())) {
            throw new DuplicatedPhoneExistenceException(
                DUPLICATED_PHONE_EXISTENCE, "phone='%s'".formatted(command.phone()));
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        Member member = Member.of(command.name(), command.email(), encodedPassword, command.phone());
        return memberRepository.save(member);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new AipiaException(ErrorCodeMessage.MEMBER_NOT_FOUND, "email='%s'".formatted(email)));
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(
                () -> new AipiaException(ErrorCodeMessage.MEMBER_NOT_FOUND, "memberId='%s'".formatted(memberId)));
    }

    /**
     * 회원 정보를 조회한다
     *
     * @param requestedMemberId     요청한 회원 ID
     * @param authenticatedMemberId 인증된 회원 ID (토큰에서 추출)
     * @throws MemberAccessForbiddenException 요청한 회원 ID와 인증된 회원 ID가 다를 경우 발생
     */
    public Member retrieveMemberById(Long requestedMemberId, Long authenticatedMemberId) {
        if (!requestedMemberId.equals(authenticatedMemberId)) {
            throw new MemberAccessForbiddenException(
                MEMBER_ACCESS_FORBIDDEN,
                "requestedMemberId='%s', authenticatedMemberId='%s'".formatted(requestedMemberId, authenticatedMemberId)
            );
        }

        return findById(requestedMemberId);
    }
}
package com.aipiabackend.member.service;

import static com.aipiabackend.support.model.ErrorCodeMessage.DUPLICATED_EMAIL_EXISTENCE;
import static com.aipiabackend.support.model.ErrorCodeMessage.DUPLICATED_PHONE_EXISTENCE;
import static com.aipiabackend.support.model.ErrorCodeMessage.MEMBER_ACCESS_FORBIDDEN;
import static com.aipiabackend.support.model.ErrorCodeMessage.WITHDRAWN_MEMBER_ACCESS_FORBIDDEN;

import com.aipiabackend.auth.model.MemberPrincipal;
import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.model.MemberGrade;
import com.aipiabackend.member.model.exception.DuplicatedEmailExistenceException;
import com.aipiabackend.member.model.exception.DuplicatedPhoneExistenceException;
import com.aipiabackend.member.model.exception.MemberAccessForbiddenException;
import com.aipiabackend.member.model.exception.WithdrawnMemberAccessForbiddenException;
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
        Member member = Member.ofMember(command.name(), command.email(), encodedPassword, command.phone());
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
     * @param requestedMemberId 요청한 회원 ID
     * @param principal         인증된 회원 정보
     * @throws MemberAccessForbiddenException 일반 회원이 본인이 아닌 다른 회원을 조회하려고 할 경우 발생
     */
    public Member retrieveMemberById(Long requestedMemberId, MemberPrincipal principal) {
        /**
         * todo: 자신의 정보를 조회하는 건 /api/members/me로 분리하고, 관리자가 회원 조회하는 건 /api/members/{memberId}로 분리하기
         * 그러면, 스프링 시큐리티의 경로별로 권한을 설정할 수 있기 때문에 회원 조회 코드 내에서 권한 분기 코드를 제거할 수 있음
         */

        // 관리자는 모든 회원 조회 가능
        if (principal.getGrade() == MemberGrade.ADMIN) {
            return findById(requestedMemberId);
        }

        // 일반 회원은 본인만 조회 가능
        if (!requestedMemberId.equals(principal.getMemberId())) {
            throw new MemberAccessForbiddenException(
                MEMBER_ACCESS_FORBIDDEN,
                "requestedMemberId='%s', authenticatedMemberId='%s'".formatted(requestedMemberId,
                    principal.getMemberId())
            );
        }

        return findById(requestedMemberId);
    }

    /**
     * 회원 본인의 정보를 조회한다 s
     *
     * @throws WithdrawnMemberAccessForbiddenException 탈퇴한 회원이 조회를 시도할 경우 발생
     */
    public Member retrieveMe(Long memberId) {
        Member member = findById(memberId);

        if (member.isWithdrawn()) {
            throw new WithdrawnMemberAccessForbiddenException(
                WITHDRAWN_MEMBER_ACCESS_FORBIDDEN,
                "memberId='%s'".formatted(memberId)
            );
        }

        return member;
    }
}
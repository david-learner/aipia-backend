package com.aipiabackend.member.service;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import com.aipiabackend.member.service.dto.MemberJoinCommand;
import com.aipiabackend.member.model.exception.DuplicatedEmailExistenceException;
import com.aipiabackend.member.model.exception.DuplicatedPhoneExistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member join(MemberJoinCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new DuplicatedEmailExistenceException("이미 존재하는 이메일입니다: " + command.email());
        }

        if (memberRepository.existsByPhone(command.phone())) {
            throw new DuplicatedPhoneExistenceException("이미 존재하는 휴대폰 번호입니다: " + command.phone());
        }

        Member member = Member.of(command.name(), command.email(), command.password(), command.phone());
        return memberRepository.save(member);
    }
}
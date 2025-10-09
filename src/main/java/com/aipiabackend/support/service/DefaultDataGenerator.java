package com.aipiabackend.support.service;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 기본 데이터 생성을 위한 클래스
 */
@Component
@RequiredArgsConstructor
public class DefaultDataGenerator implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 데이터가 없을 때만 초기 데이터 추가
        if (memberRepository.count() == 0) {
            // 1. 탈퇴하지 않은 회원
            Member activeMember = Member.ofMember(
                "박민수",
                "mspark@example.com",
                passwordEncoder.encode("password123"),
                "010-0000-0001"
            );
            memberRepository.save(activeMember);

            // 2. 탈퇴한 회원
            Member withdrawnMember = Member.ofMember(
                "최영희",
                "yhchoi@example.com",
                passwordEncoder.encode("password123"),
                "010-0000-0002"
            );
            withdrawnMember.withdraw();
            memberRepository.save(withdrawnMember);

            // 3. 관리자 회원
            Member adminMember = Member.ofAdmin(
                "관리자",
                "admin@example.com",
                passwordEncoder.encode("adminPassword123"),
                "010-0000-0003"
            );
            memberRepository.save(adminMember);
        }
    }
}

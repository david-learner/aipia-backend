package com.aipiabackend.support.service;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 기본 데이터 생성을 위한 클래스
 */
@Component
@RequiredArgsConstructor
public class DefaultDataGenerator implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
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

        // 상품 데이터가 없을 때만 초기 데이터 추가
        if (productRepository.count() == 0) {
            // 1. 노트북
            Product laptop = Product.of(
                "고성능 노트북",
                new BigDecimal("1500000.00"),
                10,
                "최신 프로세서와 16GB RAM을 장착한 고성능 노트북입니다."
            );
            productRepository.save(laptop);

            // 2. 무선 마우스
            Product mouse = Product.of(
                "무선 마우스",
                new BigDecimal("35000.00"),
                50,
                "인체공학적 디자인의 무선 마우스입니다."
            );
            productRepository.save(mouse);
        }
    }
}

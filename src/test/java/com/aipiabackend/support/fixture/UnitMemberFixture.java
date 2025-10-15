package com.aipiabackend.support.fixture;

import com.aipiabackend.member.model.Member;

/**
 * 단위 테스트용 회원(Member) 픽스처를 생성하는 헬퍼 클래스
 */
public class UnitMemberFixture {

    public static Member 기본_회원_생성() {
        return Member.ofMember("김길동", "gdkim@gmail.com", "gdkimSecret123", "010-1111-2222");
    }
}

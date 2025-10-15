package com.aipiabackend.support.fixture;

import static io.restassured.RestAssured.given;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import com.aipiabackend.support.model.LoginedAdmin;
import com.aipiabackend.support.model.LoginedMember;
import com.aipiabackend.support.util.FixtureUtil;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 인수 테스트용 회원(Member) 픽스처를 생성하는 헬퍼 클래스
 */
public class AcceptanceMemberFixture {

    /**
     * 회원 가입 및 로그인을 수행하고 액세스 토큰을 반환합니다.
     *
     * @param name     회원 이름
     * @param email    이메일
     * @param password 비밀번호
     * @param phone    전화번호
     * @return 액세스 토큰
     */
    public static LoginedMember 회원가입_및_로그인(String name, String email, String password, String phone) {
        String joinedMemberLocation = 회원_가입(name, email, password, phone);
        Long memberId = FixtureUtil.getResourceIdFromLocation(joinedMemberLocation);
        String accessToken = 로그인_후_접근_토큰_추출(email, password);

        return new LoginedMember(memberId, accessToken);
    }

    /**
     * 회원 가입 API를 호출합니다.
     *
     * @param name     회원 이름
     * @param email    이메일
     * @param password 비밀번호
     * @param phone    전화번호
     * @return Location 헤더 값
     */
    public static String 회원_가입(String name, String email, String password, String phone) {
        String signupRequestBody = String.format("""
            {
                "name": "%s",
                "email": "%s",
                "password": "%s",
                "phone": "%s"
            }
            """, name, email, password, phone);

        return given()
            .contentType(ContentType.JSON)
            .body(signupRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);
    }

    /**
     * 로그인 API를 호출하고 액세스 토큰을 추출합니다.
     *
     * @param email    이메일
     * @param password 비밀번호
     * @return 액세스 토큰
     */
    public static String 로그인_후_접근_토큰_추출(String email, String password) {
        String loginRequestBody = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, email, password);

        return given()
            .contentType(ContentType.JSON)
            .body(loginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");
    }

    /**
     * 회원 가입을 수행하고 회원 ID를 추출합니다.
     *
     * @param name     회원 이름
     * @param email    이메일
     * @param password 비밀번호
     * @param phone    전화번호
     * @return 회원 ID
     */
    public static Long 회원_가입_후_ID_추출(String name, String email, String password, String phone) {
        String location = 회원_가입(name, email, password, phone);
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    /**
     * 관리자 회원을 직접 생성하고 저장한 후 액세스 토큰을 반환합니다.
     *
     * @param memberRepository 회원 저장소
     * @param passwordEncoder  비밀번호 인코더
     * @param name             관리자 이름
     * @param email            이메일
     * @param password         비밀번호
     * @param phone            전화번호
     * @return 액세스 토큰
     */
    public static LoginedAdmin 관리자_생성_및_로그인(
        MemberRepository memberRepository,
        PasswordEncoder passwordEncoder,
        String name,
        String email,
        String password,
        String phone
    ) {
        Member admin = Member.ofAdmin(name, email, passwordEncoder.encode(password), phone);
        Member savedAdmin = memberRepository.save(admin);
        String accessToken = 로그인_후_접근_토큰_추출(email, password);

        return new LoginedAdmin(savedAdmin.getId(), accessToken);
    }

    /**
     * 기본 테스트 회원을 생성하고 액세스 토큰을 반환합니다.
     *
     * @return 액세스 토큰
     */
    public static LoginedMember 기본_회원_생성_및_로그인() {
        return 회원가입_및_로그인("김길동", "gdkim@gmail.com", "gdkimSecret123", "010-1111-2222");
    }

    /**
     * 기본 관리자를 생성하고 액세스 토큰을 반환합니다.
     *
     * @param memberRepository 회원 저장소
     * @param passwordEncoder  비밀번호 인코더
     * @return 액세스 토큰
     */
    public static LoginedAdmin 기본_관리자_생성_및_로그인(
        MemberRepository memberRepository,
        PasswordEncoder passwordEncoder
    ) {
        return 관리자_생성_및_로그인(
            memberRepository,
            passwordEncoder,
            "관리자",
            "test-admin@example.com",
            "adminSecret123",
            "010-9999-9999"
        );
    }
}

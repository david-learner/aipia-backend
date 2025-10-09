package com.aipiabackend.member.acceptance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MemberAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void 비회원은_서비스에_가입하여_회원이_된다() {
        String requestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .header(HttpHeaders.LOCATION, matchesRegex("^/api/members/\\d+$"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "일이삼사오육칠팔구십일", // 11자 - 실패
        "", // 빈 문자열 - 실패
        "abc", // 영문 - 실패
        "123", // 숫자 - 실패
        "홍길동123", // 한글+숫자 - 실패
        "홍 길 동", // 공백 포함 - 실패
        "홍길동!@#", // 특수문자 포함 - 실패
    })
    void 회원가입시_이름은_최대_10자리_한글_문자열이어야_한다(String invalidName) {
        String requestBody = String.format("""
            {
                "name": "%s",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """, invalidName);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789012345678901234567890123456789012@test.com", // 51자 - 실패
        "", // 빈 문자열 - 실패
        "invalid-email", // 이메일 형식 아님 - 실패
        "test@", // 불완전한 이메일 - 실패
        "@test.com", // @앞에 내용 없음 - 실패
    })
    void 회원가입시_이메일은_최대_50자리_이메일_형식이어야_한다(String invalidEmail) {
        String requestBody = String.format("""
            {
                "name": "김길동",
                "email": "%s",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """, invalidEmail);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123456789012345678901", // 21자 - 실패
        "", // 빈 문자열 - 실패
        "password!@#", // 특수문자 포함 - 실패
        "비밀번호123", // 한글 포함 - 실패
        "password 123", // 공백 포함 - 실패
    })
    void 회원가입시_비밀번호는_최대_20자리_영문과_숫자만_허용한다(String invalidPassword) {
        String requestBody = String.format("""
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "%s",
                "phone": "010-1111-2222"
            }
            """, invalidPassword);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", // 빈 문자열 - 실패
        "010-12345-6789", // 잘못된 형식 - 실패
        "010-123-4567", // 잘못된 형식 - 실패
        "011-1234-5678", // 010으로 시작하지 않음 - 실패
        "010-abcd-efgh", // 숫자가 아님 - 실패
        "010 1234 5678", // 공백 포함 - 실패
        "010.1234.5678", // 점(.) 포함 - 실패
    })
    void 회원가입시_휴대폰_번호는_010_xxxx_xxxx_형식이어야_한다(String invalidPhone) {
        String requestBody = String.format("""
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "%s"
            }
            """, invalidPhone);

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 이미_존재하는_휴대폰_번호로_회원가입시_409_Conflict_응답한다() {
        String firstMemberRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(firstMemberRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        String duplicatePhoneRequestBody = """
            {
                "name": "홍길동",
                "email": "gdhong@gmail.com", 
                "password": "gdhongSecret123",
                "phone": "010-1111-2222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(duplicatePhoneRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("code", equalTo("AIPIA-0001"))
            .body("message", equalTo("이미 존재하는 휴대폰 번호입니다."));
    }

    @Test
    void 이미_존재하는_이메일로_회원가입시_409_Conflict_응답한다() {
        String firstMemberRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(firstMemberRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        String duplicateEmailRequestBody = """
            {
                "name": "홍길동",
                "email": "gdkim@gmail.com",
                "password": "gdhongSecret123",
                "phone": "010-3333-4444"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(duplicateEmailRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("code", equalTo("AIPIA-0002"))
            .body("message", equalTo("이미 존재하는 이메일입니다."));
    }

    @Test
    void 회원은_본인의_정보를_조회할_수_있다() {
        // 회원가입
        String signupRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        String location = given()
            .contentType(ContentType.JSON)
            .body(signupRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // 로그인하여 JWT 토큰 획득
        String loginRequestBody = """
            {
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123"
            }
            """;

        String accessToken = given()
            .contentType(ContentType.JSON)
            .body(loginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // 회원 정보 조회
        given()
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .get(location)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo("김길동"))
            .body("email", equalTo("gdkim@gmail.com"))
            .body("phone", equalTo("010-1111-2222"))
            .body("grade", equalTo("MEMBER"))
            .body("joinedAt", matchesRegex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*$"));
    }

    @Test
    void Authorization_헤더_없이_회원_조회시_401_Unauthorized_응답한다() {
        // 회원가입
        String signupRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        String location = given()
            .contentType(ContentType.JSON)
            .body(signupRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // Authorization 헤더 없이 회원 정보 조회
        given()
            .when()
            .get(location)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 본인이_아닌_다른_회원_조회시_403_Forbidden을_응답한다() {
        // 첫 번째 회원 가입
        String firstMemberRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        String firstMemberLocation = given()
            .contentType(ContentType.JSON)
            .body(firstMemberRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // 두 번째 회원 가입
        String secondMemberRequestBody = """
            {
                "name": "홍길동",
                "email": "gdhong@gmail.com",
                "password": "gdhongSecret123",
                "phone": "010-3333-4444"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(secondMemberRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        // 두 번째 회원으로 로그인
        String secondMemberLoginRequestBody = """
            {
                "email": "gdhong@gmail.com",
                "password": "gdhongSecret123"
            }
            """;

        String secondMemberAccessToken = given()
            .contentType(ContentType.JSON)
            .body(secondMemberLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // 두 번째 회원의 토큰으로 첫 번째 회원 정보 조회 시도
        given()
            .header("Authorization", "Bearer " + secondMemberAccessToken)
            .when()
            .get(firstMemberLocation)
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void 관리자는_다른_회원의_정보를_조회할_수_있다() {
        // 일반 회원 가입
        String memberRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        String memberLocation = given()
            .contentType(ContentType.JSON)
            .body(memberRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // 관리자 회원 직접 생성
        Member admin = Member.ofAdmin(
            "관리자",
            "admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-9999-9999"
        );
        memberRepository.save(admin);

        // 관리자로 로그인
        String adminLoginRequestBody = """
            {
                "email": "admin@example.com",
                "password": "adminSecret123"
            }
            """;

        String adminAccessToken = given()
            .contentType(ContentType.JSON)
            .body(adminLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // 관리자 토큰으로 일반 회원 정보 조회
        given()
            .header("Authorization", "Bearer " + adminAccessToken)
            .when()
            .get(memberLocation)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("name", equalTo("김길동"))
            .body("email", equalTo("gdkim@gmail.com"))
            .body("phone", equalTo("010-1111-2222"))
            .body("grade", equalTo("MEMBER"));
    }
}

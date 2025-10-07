package com.aipiabackend.member;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.aipiabackend.member.controller.MemberRestController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(controllers = MemberRestController.class)
public class MemberRestControllerTest {

    @Autowired
    MockMvcTester mockMvcTester;

    @Test
    void 비회원은_서비스에_가입하여_회원이_된다() {
        String requestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkim-secret",
                "phone": "010-1111-2222"
            }
            """;

        var response = mockMvcTester
            .post()
            .uri("/api/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        assertThat(response).hasStatus(HttpStatus.CREATED);
        // Location 헤더의 값이 /api/members/{id} 형태인지 검증
        assertThat(response).headers().matches(httpHeaders -> {
            String location = httpHeaders.getFirst(HttpHeaders.LOCATION);
            return location != null && location.matches("^/api/members/\\d+$");
        });
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
                "password": "gdkim-secret",
                "phone": "010-1111-2222"
            }
            """, invalidName);

        var response = mockMvcTester
            .post()
            .uri("/api/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
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
                "name": "홍길동",
                "email": "%s",
                "password": "gdkim-secret",
                "phone": "010-1111-2222"
            }
            """, invalidEmail);

        var response = mockMvcTester
            .post()
            .uri("/api/members")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        assertThat(response).hasStatus(HttpStatus.BAD_REQUEST);
    }
}

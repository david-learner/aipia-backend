package com.aipiabackend.member;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.aipiabackend.member.controller.MemberRestController;
import org.junit.jupiter.api.Test;
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
}

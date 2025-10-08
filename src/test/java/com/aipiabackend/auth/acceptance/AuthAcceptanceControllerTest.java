package com.aipiabackend.auth.acceptance;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthAcceptanceControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void 존재하지_않는_이메일로_로그인시_실패한다() {
        String loginRequestBody = """
            {
                "email": "notexist@gmail.com",
                "password": "password123"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(loginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 잘못된_비밀번호로_로그인시_실패한다() {
        // 먼저 회원가입
        String signupRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(signupRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        // 잘못된 비밀번호로 로그인
        String loginRequestBody = """
            {
                "email": "gdkim@gmail.com",
                "password": "wrongPassword123"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(loginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 회원은_유효한_이메일과_비밀번호로_로그인하여_액세스_토큰을_발급받는다() {
        // 먼저 회원가입
        String signupRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(signupRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value());

        // 로그인하여 JWT 토큰 획득
        String loginRequestBody = """
            {
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123"
            }
            """;

        Response response = given()
            .contentType(ContentType.JSON)
            .body(loginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();

        String accessToken = response.jsonPath().getString("accessToken");

        // JWT 파싱 및 검증 (서명 검증)
        String jwtSecret = "mySecretKey123456789012345678901234567890";
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(accessToken)
            .getPayload();

        // 클레임 검증
        assertNotNull(claims.getSubject(), "JWT subject should not be null");
        assertNotNull(claims.getIssuedAt(), "JWT issued at should not be null");
        assertNotNull(claims.getExpiration(), "JWT expiration should not be null");
    }
}

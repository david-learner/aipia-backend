package com.aipiabackend.order.acceptance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
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
public class OrderAcceptanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    void 회원이_상품을_주문한다() {
        // 회원 가입
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

        // 로그인
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

        // 주문 생성
        String orderRequestBody = """
            {
                "memberId": 1,
                "orderLines": [
                    {
                        "productId": 1,
                        "productQuantity": 2,
                        "productPrice": 1500000.00,
                        "amount": 3000000.00
                    }
                ],
                "amount": 3000000.00
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .header(HttpHeaders.LOCATION, matchesRegex("^/api/orders/\\d+$"));
    }

    @Test
    void 주문의_총_금액이_올바르게_계산된다() {
        // 회원 가입 및 로그인
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

        // 복수 상품을 포함한 주문 생성
        String orderRequestBody = """
            {
                "memberId": 1,
                "orderLines": [
                    {
                        "productId": 1,
                        "productQuantity": 2,
                        "productPrice": 1500000.00,
                        "amount": 3000000.00
                    },
                    {
                        "productId": 2,
                        "productQuantity": 3,
                        "productPrice": 35000.00,
                        "amount": 105000.00
                    }
                ],
                "amount": 3105000.00
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .header(HttpHeaders.LOCATION, matchesRegex("^/api/orders/\\d+$"));
    }

    @Test
    void Authorization_헤더_없이_주문_생성시_401_Unauthorized_응답한다() {
        String orderRequestBody = """
            {
                "memberId": 1,
                "orderLines": [
                    {
                        "productId": 1,
                        "productQuantity": 2,
                        "productPrice": 1500000.00,
                        "amount": 3000000.00
                    }
                ],
                "amount": 3000000.00
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 필수_필드_memberId_누락시_400_Bad_Request_응답한다() {
        // 회원 가입 및 로그인
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

        // memberId 필드를 null로 설정
        String orderRequestBody = """
            {
                "memberId": null,
                "orderLines": [
                    {
                        "productId": 1,
                        "productQuantity": 2,
                        "productPrice": 1500000.00,
                        "amount": 3000000.00
                    }
                ],
                "amount": 3000000.00
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 필수_필드_orderLines_누락시_400_Bad_Request_응답한다() {
        // 회원 가입 및 로그인
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

        // orderLines 필드를 빈 배열로 설정
        String orderRequestBody = """
            {
                "memberId": 1,
                "orderLines": [],
                "amount": 3000000.00
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 필수_필드_amount_누락시_400_Bad_Request_응답한다() {
        // 회원 가입 및 로그인
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

        // amount 필드를 null로 설정
        String orderRequestBody = """
            {
                "memberId": 1,
                "orderLines": [
                    {
                        "productId": 1,
                        "productQuantity": 2,
                        "productPrice": 1500000.00,
                        "amount": 3000000.00
                    }
                ],
                "amount": null
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}

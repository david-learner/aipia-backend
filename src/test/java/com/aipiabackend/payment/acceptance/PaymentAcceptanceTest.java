package com.aipiabackend.payment.acceptance;

import static io.restassured.RestAssured.given;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.member.repository.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class PaymentAcceptanceTest {

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
    void 회원이_주문을_결제한다() {
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

        // 관리자 회원 생성 (상품 생성을 위해)
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        // 관리자 로그인
        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
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

        // 상품 생성
        String productRequestBody = """
            {
                "name": "맥북 프로",
                "price": 1500000,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.CREATED.value());

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
            .statusCode(HttpStatus.CREATED.value());

        // 결제
        String payRequestBody = """
            {
                "orderId": 1,
                "cardNumber": "1234567890123456",
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": "01"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    void Authorization_헤더_없이_결제_시도시_401_Unauthorized_응답한다() {
        String payRequestBody = """
            {
                "orderId": 1,
                "cardNumber": "1234567890123456",
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": "01"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 필수_필드_orderId_누락시_400_Bad_Request_응답한다() {
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

        // orderId 필드를 null로 설정
        String payRequestBody = """
            {
                "orderId": null,
                "cardNumber": "1234567890123456",
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": "01"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 필수_필드_cardNumber_누락시_400_Bad_Request_응답한다() {
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

        // cardNumber 필드를 null로 설정
        String payRequestBody = """
            {
                "orderId": 1,
                "cardNumber": null,
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": "01"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 필수_필드_cardExpirationYearAndMonth_누락시_400_Bad_Request_응답한다() {
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

        // cardExpirationYearAndMonth 필드를 null로 설정
        String payRequestBody = """
            {
                "orderId": 1,
                "cardNumber": "1234567890123456",
                "cardExpirationYearAndMonth": null,
                "cardIssuerCode": "01"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 필수_필드_cardIssuerCode_누락시_400_Bad_Request_응답한다() {
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

        // cardIssuerCode 필드를 null로 설정
        String payRequestBody = """
            {
                "orderId": 1,
                "cardNumber": "1234567890123456",
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": null
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 존재하지_않는_주문_ID로_결제_시도시_500_Internal_Server_Error_응답한다() {
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

        // 존재하지 않는 주문 ID로 결제 시도
        String payRequestBody = """
            {
                "orderId": 999,
                "cardNumber": "1234567890123456",
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": "01"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 이미_결제_완료된_주문에_대해_재결제_시도시_500_Internal_Server_Error_응답한다() {
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

        // 관리자 회원 생성 (상품 생성을 위해)
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        // 관리자 로그인
        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
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

        // 상품 생성
        String productRequestBody = """
            {
                "name": "맥북 프로",
                "price": 1500000,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.CREATED.value());

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
            .statusCode(HttpStatus.CREATED.value());

        // 첫 번째 결제 (성공)
        String payRequestBody = """
            {
                "orderId": 1,
                "cardNumber": "1234567890123456",
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": "01"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.OK.value());

        // 동일 주문에 대해 두 번째 결제 시도 (실패 예상)
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 결제_재시도_3회_소진시_500_Internal_Server_Error_응답한다() {
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

        // 관리자 회원 생성 (상품 생성을 위해)
        Member admin = Member.ofAdmin(
            "테스트관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-8888-8888"
        );
        memberRepository.save(admin);

        // 관리자 로그인
        String adminLoginRequestBody = """
            {
                "email": "test-admin@example.com",
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

        // 상품 생성
        String productRequestBody = """
            {
                "name": "맥북 프로",
                "price": 1500000,
                "stock": 10,
                "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
            .contentType(ContentType.JSON)
            .body(productRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.CREATED.value());

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
            .statusCode(HttpStatus.CREATED.value());

        // 항상 실패하는 카드 번호로 결제 시도 (재시도 3회 소진 후 실패)
        String payRequestBody = """
            {
                "orderId": 1,
                "cardNumber": "0000000000000000",
                "cardExpirationYearAndMonth": "1225",
                "cardIssuerCode": "01"
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

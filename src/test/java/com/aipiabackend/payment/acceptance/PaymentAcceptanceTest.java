package com.aipiabackend.payment.acceptance;

import static com.aipiabackend.support.fixture.OrderFixture.*;
import static io.restassured.RestAssured.given;

import com.aipiabackend.payment.controller.dto.PaymentCreateRequest;
import com.aipiabackend.support.AcceptanceTestBase;
import com.aipiabackend.support.fixture.MemberFixture;
import com.aipiabackend.support.fixture.OrderFixture;
import com.aipiabackend.support.model.LoginedMember;
import com.aipiabackend.support.model.OrderAndMember;
import com.aipiabackend.support.util.FixtureUtil;
import io.restassured.http.ContentType;
import java.util.stream.Stream;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

public class PaymentAcceptanceTest extends AcceptanceTestBase {

    @Test
    void 회원이_주문을_결제한다() {
        OrderAndMember 기본_주문_및_기본_회원 = 기본_주문_및_기본_회원_생성(objectMapper, productRepository);
        LoginedMember 기본_회원 = 기본_주문_및_기본_회원.member();

        Long orderId = FixtureUtil.getResourceIdFromLocation(기본_주문_및_기본_회원.orderLocation());
        var paymentCreateRequest = new PaymentCreateRequest(
            orderId,
            "1234567890123456",
            "1225",
            "01"
        );
        String paymentCreateRequestBody = FixtureUtil.getJsonFrom(objectMapper, paymentCreateRequest);

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본_회원.accessToken())
            .contentType(ContentType.JSON)
            .body(paymentCreateRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    /**
     * 필수 필드 누락 시 400 Bad Request를 응답하는지 검증
     *
     * @param 테스트명        테스트 케이스 설명
     * @param requestBody 누락된 필드가 포함된 요청 본문
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("필수_필드_누락_케이스")
    void 필수_필드_누락시_400_Bad_Request_응답한다(String 테스트명, String requestBody) {
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

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * 필수 필드 누락 테스트 케이스 제공
     *
     * @return 테스트 케이스별 테스트명과 요청 본문
     */
    private static Stream<Arguments> 필수_필드_누락_케이스() {
        return Stream.of(
            Arguments.of(
                "orderId 누락",
                """
                    {
                        "orderId": null,
                        "cardNumber": "1234567890123456",
                        "cardExpirationYearAndMonth": "1225",
                        "cardIssuerCode": "01"
                    }
                    """
            ),
            Arguments.of(
                "cardNumber 누락",
                """
                    {
                        "orderId": 1,
                        "cardNumber": null,
                        "cardExpirationYearAndMonth": "1225",
                        "cardIssuerCode": "01"
                    }
                    """
            ),
            Arguments.of(
                "cardExpirationYearAndMonth 누락",
                """
                    {
                        "orderId": 1,
                        "cardNumber": "1234567890123456",
                        "cardExpirationYearAndMonth": null,
                        "cardIssuerCode": "01"
                    }
                    """
            ),
            Arguments.of(
                "cardIssuerCode 누락",
                """
                    {
                        "orderId": 1,
                        "cardNumber": "1234567890123456",
                        "cardExpirationYearAndMonth": "1225",
                        "cardIssuerCode": null
                    }
                    """
            )
        );
    }

    @Test
    void 존재하지_않는_주문_ID로_결제_시도시_500_Internal_Server_Error_응답한다() {
        LoginedMember 기본_회원 = MemberFixture.기본_회원_생성_및_로그인();

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
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본_회원.accessToken())
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 이미_결제_완료된_주문에_대해_재결제_시도시_500_Internal_Server_Error_응답한다() {
        OrderAndMember 기본_주문_및_기본_회원 = 기본_주문_및_기본_회원_생성(objectMapper, productRepository);
        LoginedMember 기본_회원 = 기본_주문_및_기본_회원.member();

        Long orderId = FixtureUtil.getResourceIdFromLocation(기본_주문_및_기본_회원.orderLocation());
        var paymentCreateRequest = new PaymentCreateRequest(
            orderId,
            "1234567890123456",
            "1225",
            "01"
        );
        String paymentCreateRequestBody = FixtureUtil.getJsonFrom(objectMapper, paymentCreateRequest);

        // 첫 번째 결제 (성공)
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본_회원.accessToken())
            .contentType(ContentType.JSON)
            .body(paymentCreateRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.OK.value());

        // 동일 주문에 대해 두 번째 결제 시도 (실패 예상)
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본_회원.accessToken())
            .contentType(ContentType.JSON)
            .body(paymentCreateRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 결제_재시도_3회_소진시_500_Internal_Server_Error_응답한다() {
        OrderAndMember 기본_주문_및_기본_회원 = 기본_주문_및_기본_회원_생성(objectMapper, productRepository);
        LoginedMember 기본_회원 = 기본_주문_및_기본_회원.member();

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
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본_회원.accessToken())
            .contentType(ContentType.JSON)
            .body(payRequestBody)
            .when()
            .post("/api/payments")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

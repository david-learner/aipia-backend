package com.aipiabackend.order.acceptance;

import static com.aipiabackend.support.fixture.MemberFixture.기본_관리자_생성_및_로그인;
import static com.aipiabackend.support.fixture.MemberFixture.기본_회원_생성_및_로그인;
import static com.aipiabackend.support.fixture.MemberFixture.회원가입_및_로그인;
import static com.aipiabackend.support.fixture.OrderFixture.주문_생성;
import static com.aipiabackend.support.fixture.ProductFixture.노트북_생성;
import static com.aipiabackend.support.fixture.ProductFixture.마우스_생성;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;

import com.aipiabackend.order.controller.dto.OrderCreateRequest;
import com.aipiabackend.order.controller.dto.OrderLineCreateRequest;
import com.aipiabackend.order.model.OrderStatus;
import com.aipiabackend.product.model.Product;
import com.aipiabackend.support.AcceptanceTestBase;
import com.aipiabackend.support.model.ErrorCodeMessage;
import com.aipiabackend.support.model.LoginedAdmin;
import com.aipiabackend.support.model.LoginedMember;
import com.aipiabackend.support.util.FixtureUtil;
import io.restassured.http.ContentType;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class OrderAcceptanceTest extends AcceptanceTestBase {

    @Test
    void 회원이_상품을_주문한다() {
        Product 노트북 = 노트북_생성(productRepository);
        LoginedMember 기본회원 = 기본_회원_생성_및_로그인();
        int 상품수량 = 2;
        long 총_주문_금액 = 노트북.getPrice() * 상품수량;

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(
            기본회원.memberId(),
            List.of(OrderLineCreateRequest.of(노트북, 상품수량)),
            총_주문_금액
        );
        String orderRequestBody = FixtureUtil.getJsonFrom(objectMapper, orderCreateRequest);

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본회원.accessToken())
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .header(HttpHeaders.LOCATION, matchesRegex("^/api/orders/\\d+$"));
    }

    @Test
    void 필수_필드_memberId_누락시_400_Bad_Request_응답한다() {
        LoginedMember 기본회원 = 기본_회원_생성_및_로그인();

        // memberId 필드를 null로 설정
        String orderRequestBody = """
            {
                "memberId": null,
                "orderLines": [
                    {
                        "productId": 1,
                        "productQuantity": 2,
                        "productPrice": 1500000,
                        "amount": 3000000
                    }
                ],
                "amount": 3000000
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본회원.accessToken())
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 필수_필드_orderLines_누락시_400_Bad_Request_응답한다() {
        LoginedMember 기본회원 = 기본_회원_생성_및_로그인();

        // orderLines 필드를 빈 배열로 설정
        String orderRequestBody = """
            {
                "memberId": 1,
                "orderLines": [],
                "amount": 3000000
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본회원.accessToken())
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 필수_필드_amount_누락시_400_Bad_Request_응답한다() {
        LoginedMember 기본회원 = 기본_회원_생성_및_로그인();

        // amount 필드를 null로 설정
        String orderRequestBody = """
            {
                "memberId": 1,
                "orderLines": [
                    {
                        "productId": 1,
                        "productQuantity": 2,
                        "productPrice": 1500000,
                        "amount": 3000000
                    }
                ],
                "amount": null
            }
            """;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본회원.accessToken())
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 회원이_자신의_주문을_조회한다() {
        Product 노트북 = 노트북_생성(productRepository);
        int 주문_수량 = 2;
        LoginedMember 주문자 = 기본_회원_생성_및_로그인();
        String 주문_Location = 주문_생성(objectMapper, 주문자, 노트북, 주문_수량);
        int 총_주문_금액 = 노트북.getPrice().intValue() * 주문_수량;

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 주문자.accessToken())
            .when()
            .get(주문_Location)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("memberId", equalTo(주문자.memberId().intValue()))
            .body("status", equalTo(OrderStatus.PENDING.name()))
            .body("amount", equalTo(총_주문_금액))
            .body("orderLines.size()", equalTo(1))
            .body("orderLines[0].productId", equalTo(노트북.getId().intValue()))
            .body("orderLines[0].productQuantity", equalTo(주문_수량))
            .body("orderLines[0].productPrice", equalTo(노트북.getPrice().intValue()))
            .body("orderLines[0].amount", equalTo(총_주문_금액));
    }

    @Test
    void 회원이_다른_회원의_주문을_조회하면_403_Forbidden을_응답한다() {
        LoginedMember 첫번째_회원 = 회원가입_및_로그인("김길동", "gdkim@gmail.com", "gdkimSecret123", "010-1111-2222");
        LoginedMember 두번째_회원 = 회원가입_및_로그인("나길동", "gdNa@gmail.com", "gdNaSecret123", "010-2222-3333");

        Product 마우스 = 마우스_생성(productRepository);

        String 첫번째_회원의_주문_Location = 주문_생성(objectMapper, 첫번째_회원, 마우스, 2);
        주문_생성(objectMapper, 두번째_회원, 마우스, 4);

        // 두 번째 회원이 첫 번째 회원의 주문 조회 시도
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 두번째_회원.accessToken())
            .when()
            .get(첫번째_회원의_주문_Location)
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("code", equalTo(ErrorCodeMessage.ORDER_ACCESS_FORBIDDEN.code()))
            .body("message", equalTo(ErrorCodeMessage.ORDER_ACCESS_FORBIDDEN.message()));
    }

    @Test
    void 관리자는_모든_회원의_주문을_조회할_수_있다() {
        LoginedMember 첫번째_회원 = 회원가입_및_로그인("김길동", "gdkim@gmail.com", "gdkimSecret123", "010-1111-2222");
        LoginedMember 두번째_회원 = 회원가입_및_로그인("나길동", "gdNa@gmail.com", "gdNaSecret123", "010-2222-3333");

        Product 마우스 = 마우스_생성(productRepository);
        int 첫번째_회원의_주문_수량 = 2;
        int 두번째_회원의_주문_수량 = 4;

        String 첫번째_회원의_주문_Location = 주문_생성(objectMapper, 첫번째_회원, 마우스, 첫번째_회원의_주문_수량);
        String 두번째_회원의_주문_Location = 주문_생성(objectMapper, 두번째_회원, 마우스, 두번째_회원의_주문_수량);

        int 첫번째_회원의_총_주문금액 = 마우스.getPrice().intValue() * 첫번째_회원의_주문_수량;
        int 두번째_회원의_총_주문금액 = 마우스.getPrice().intValue() * 두번째_회원의_주문_수량;

        LoginedAdmin 관리자 = 기본_관리자_생성_및_로그인(memberRepository, passwordEncoder);

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 관리자.accessToken())
            .when()
            .get(첫번째_회원의_주문_Location)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("memberId", equalTo(첫번째_회원.memberId().intValue()))
            .body("status", equalTo(OrderStatus.PENDING.name()))
            .body("amount", equalTo(첫번째_회원의_총_주문금액));

        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 관리자.accessToken())
            .when()
            .get(두번째_회원의_주문_Location)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("memberId", equalTo(두번째_회원.memberId().intValue()))
            .body("status", equalTo(OrderStatus.PENDING.name()))
            .body("amount", equalTo(두번째_회원의_총_주문금액));
    }

    @Test
    void 존재하지_않는_주문_조회시_에러를_응답한다() {
        LoginedMember 기본회원 = 기본_회원_생성_및_로그인();

        // 존재하지 않는 주문 조회
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + 기본회원.accessToken())
            .when()
            .get("/api/orders/9999")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("code", equalTo("AIPIA-0016"))
            .body("message", equalTo("주문을 찾을 수 없습니다."));
    }
}

package com.aipiabackend.support.fixture;

import static io.restassured.RestAssured.given;

import com.aipiabackend.order.controller.dto.OrderCreateRequest;
import com.aipiabackend.order.controller.dto.OrderLineCreateRequest;
import com.aipiabackend.product.model.Product;
import com.aipiabackend.product.repository.ProductRepository;
import com.aipiabackend.support.model.LoginedMember;
import com.aipiabackend.support.model.OrderAndMember;
import com.aipiabackend.support.util.FixtureUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * 테스트용 주문(Order) 픽스처를 생성하는 헬퍼 클래스
 */
public class OrderFixture {

    /**
     * 단일 상품에 대한 주문을 생성합니다.
     *
     * @param objectMapper  ObjectMapper 인스턴스
     * @param loginedMember 로그인 한 회원 정보
     * @param product       상품
     * @param quantity      상품 수량
     * @return Location 헤더 값 (생성된 주문의 경로)
     */
    public static String 주문_생성(
        ObjectMapper objectMapper,
        LoginedMember loginedMember,
        Product product,
        int quantity
    ) {
        long price = product.getPrice();
        long amount = price * quantity;
        OrderCreateRequest request = new OrderCreateRequest(
            loginedMember.memberId(),
            List.of(OrderLineCreateRequest.of(product, quantity)),
            amount
        );
        String requestBody = FixtureUtil.getJsonFrom(objectMapper, request);

        return given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginedMember.accessToken())
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);
    }

    /**
     * 복수 상품에 대한 주문을 생성합니다.
     *
     * @param objectMapper  ObjectMapper 인스턴스
     * @param loginedMember 로그인 한 회원 정보
     * @param orderLines    주문 라인 목록
     * @param totalAmount   총 주문 금액
     * @return Location 헤더 값 (생성된 주문의 경로)
     */
    public static String 주문_생성_복수상품(
        ObjectMapper objectMapper,
        LoginedMember loginedMember,
        List<OrderLineCreateRequest> orderLines,
        long totalAmount
    ) {
        OrderCreateRequest request = new OrderCreateRequest(loginedMember.memberId(), orderLines, totalAmount);
        String requestBody = FixtureUtil.getJsonFrom(objectMapper, request);

        return given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginedMember.accessToken())
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);
    }

    /**
     * 기본 회원과 기본 상품으로 주문을 생성합니다.
     *
     * @param objectMapper      ObjectMapper 인스턴스
     * @param productRepository 상품 저장소
     * @return OrderAndMember 객체 (생성된 주문과 회원 정보 포함)
     */
    public static OrderAndMember 기본_주문_생성(
        ObjectMapper objectMapper,
        ProductRepository productRepository
    ) {
        Product product = ProductFixture.노트북_생성(productRepository);
        LoginedMember loginedMember = MemberFixture.기본_회원_생성_및_로그인();

        String orderLocation = 주문_생성(
            objectMapper,
            loginedMember,
            product,
            2
        );

        return new OrderAndMember(orderLocation, loginedMember);
    }
}

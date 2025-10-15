package com.aipiabackend.product.acceptance;

import static io.restassured.RestAssured.given;

import com.aipiabackend.product.controller.dto.ProductCreateRequest;
import com.aipiabackend.support.AcceptanceTestBase;
import com.aipiabackend.support.fixture.AcceptanceMemberFixture;
import com.aipiabackend.support.model.LoginedAdmin;
import com.aipiabackend.support.model.LoginedMember;
import com.aipiabackend.support.util.FixtureUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public class ProductAcceptanceTest extends AcceptanceTestBase {

    @Test
    void 관리자가_유효한_상품_정보로_상품을_생성한다() {
        LoginedAdmin 기본_관리자 = AcceptanceMemberFixture.기본_관리자_생성_및_로그인(memberRepository, passwordEncoder);
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            "맥북 프로",
            2500000L,
            10,
            "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
        );
        String productCreateRequestBody = FixtureUtil.getJsonFrom(objectMapper, productCreateRequest);

        given()
            .header("Authorization", "Bearer " + 기본_관리자.accessToken())
            .contentType(ContentType.JSON)
            .body(productCreateRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 일반_회원이_상품_생성을_시도하면_403_Forbidden을_응답한다() {
        LoginedMember 기본_회원 = AcceptanceMemberFixture.기본_회원_생성_및_로그인();
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            "맥북 프로",
            2500000L,
            10,
            "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
        );
        String productCreateRequestBody = FixtureUtil.getJsonFrom(objectMapper, productCreateRequest);

        given()
            .header("Authorization", "Bearer " + 기본_회원.accessToken())
            .contentType(ContentType.JSON)
            .body(productCreateRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void Authorization_헤더_없이_상품_생성_시_401_Unauthorized를_응답한다() {
        ProductCreateRequest productCreateRequest = new ProductCreateRequest(
            "맥북 프로",
            2500000L,
            10,
            "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
        );
        String productCreateRequestBody = FixtureUtil.getJsonFrom(objectMapper, productCreateRequest);

        given()
            .contentType(ContentType.JSON)
            .body(productCreateRequestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @ParameterizedTest
    @MethodSource("필수_필드_누락_케이스")
    void 필수_필드_누락_시_400_Bad_Request를_응답한다(String fieldName, String requestBody) {
        LoginedAdmin 기본_관리자 = AcceptanceMemberFixture.기본_관리자_생성_및_로그인(memberRepository, passwordEncoder);

        given()
            .header("Authorization", "Bearer " + 기본_관리자.accessToken())
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/api/products")
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private static Stream<Arguments> 필수_필드_누락_케이스() {
        return Stream.of(
            Arguments.of("name 누락", """
                {
                    "name": null,
                    "price": 2500000,
                    "stock": 10,
                    "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
                }
                """),
            Arguments.of("price 누락", """
                {
                    "name": "맥북 프로",
                    "price": null,
                    "stock": 10,
                    "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
                }
                """),
            Arguments.of("stock 누락", """
                {
                    "name": "맥북 프로",
                    "price": 2500000,
                    "stock": null,
                    "description": "Apple M3 Pro 칩, 18GB RAM, 512GB SSD"
                }
                """),
            Arguments.of("description 누락", """
                {
                    "name": "맥북 프로",
                    "price": 2500000,
                    "stock": 10,
                    "description": null
                }
                """)
        );
    }
}

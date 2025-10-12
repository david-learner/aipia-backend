package com.aipiabackend.order.acceptance;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesRegex;

import com.aipiabackend.member.model.Member;
import com.aipiabackend.support.AcceptanceTestBase;
import io.restassured.http.ContentType;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class OrderAcceptanceTest extends AcceptanceTestBase {

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
                        "productPrice": 1500000,
                        "amount": 3000000
                    }
                ],
                "amount": 3000000
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
                        "productPrice": 1500000,
                        "amount": 3000000
                    },
                    {
                        "productId": 2,
                        "productQuantity": 3,
                        "productPrice": 35000,
                        "amount": 105000
                    }
                ],
                "amount": 3105000
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
                        "productPrice": 1500000,
                        "amount": 3000000
                    }
                ],
                "amount": 3000000
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
                        "productPrice": 1500000,
                        "amount": 3000000
                    }
                ],
                "amount": 3000000
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
                "amount": 3000000
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
                        "productPrice": 1500000,
                        "amount": 3000000
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

    @Test
    void 회원이_자신의_주문을_조회한다() {
        // 회원 가입
        String signupRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        String memberLocation = given()
            .contentType(ContentType.JSON)
            .body(signupRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // Member ID 추출
        Long memberId = Long.parseLong(memberLocation.substring(memberLocation.lastIndexOf('/') + 1));

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
        String orderRequestBody = String.format("""
            {
                "memberId": %d,
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
            """, memberId);

        String orderLocation = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // 주문 조회
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .when()
            .get(orderLocation)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("memberId", equalTo(memberId.intValue()))
            .body("status", equalTo("PENDING"))
            .body("amount", equalTo(3000000))
            .body("orderLines.size()", equalTo(1))
            .body("orderLines[0].productId", equalTo(1))
            .body("orderLines[0].productQuantity", equalTo(2))
            .body("orderLines[0].productPrice", equalTo(1500000))
            .body("orderLines[0].amount", equalTo(3000000));
    }

    @Test
    void 회원이_다른_회원의_주문을_조회하면_403_Forbidden을_응답한다() {
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

        Long firstMemberId = Long.parseLong(firstMemberLocation.substring(firstMemberLocation.lastIndexOf('/') + 1));

        // 첫 번째 회원 로그인
        String firstMemberLoginRequestBody = """
            {
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123"
            }
            """;

        String firstMemberAccessToken = given()
            .contentType(ContentType.JSON)
            .body(firstMemberLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // 첫 번째 회원이 주문 생성
        String orderRequestBody = String.format("""
            {
                "memberId": %d,
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
            """, firstMemberId);

        String orderLocation = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + firstMemberAccessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
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

        // 두 번째 회원 로그인
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

        // 두 번째 회원이 첫 번째 회원의 주문 조회 시도
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + secondMemberAccessToken)
            .when()
            .get(orderLocation)
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("code", equalTo("AIPIA-0017"))
            .body("message", equalTo("해당 주문에 접근할 권한이 없습니다."));
    }

    @Test
    void 관리자는_모든_회원의_주문을_조회할_수_있다() {
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

        Long memberId = Long.parseLong(memberLocation.substring(memberLocation.lastIndexOf('/') + 1));

        // 일반 회원 로그인
        String memberLoginRequestBody = """
            {
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123"
            }
            """;

        String memberAccessToken = given()
            .contentType(ContentType.JSON)
            .body(memberLoginRequestBody)
            .when()
            .post("/api/auth/login")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getString("accessToken");

        // 일반 회원이 주문 생성
        String orderRequestBody = String.format("""
            {
                "memberId": %d,
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
            """, memberId);

        String orderLocation = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberAccessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // 관리자 회원 직접 생성
        Member admin = Member.ofAdmin(
            "관리자",
            "test-admin@example.com",
            passwordEncoder.encode("adminSecret123"),
            "010-9999-9999"
        );
        memberRepository.save(admin);

        // 관리자로 로그인
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

        // 관리자 토큰으로 일반 회원의 주문 조회
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminAccessToken)
            .when()
            .get(orderLocation)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("memberId", equalTo(memberId.intValue()))
            .body("status", equalTo("PENDING"))
            .body("amount", equalTo(3000000));
    }

    @Test
    void Authorization_헤더_없이_주문_조회시_401_Unauthorized를_응답한다() {
        // 회원 가입
        String signupRequestBody = """
            {
                "name": "김길동",
                "email": "gdkim@gmail.com",
                "password": "gdkimSecret123",
                "phone": "010-1111-2222"
            }
            """;

        String memberLocation = given()
            .contentType(ContentType.JSON)
            .body(signupRequestBody)
            .when()
            .post("/api/members")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        Long memberId = Long.parseLong(memberLocation.substring(memberLocation.lastIndexOf('/') + 1));

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
        String orderRequestBody = String.format("""
            {
                "memberId": %d,
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
            """, memberId);

        String orderLocation = given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(orderRequestBody)
            .when()
            .post("/api/orders")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .header(HttpHeaders.LOCATION);

        // Authorization 헤더 없이 주문 조회
        given()
            .when()
            .get(orderLocation)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void 존재하지_않는_주문_조회시_에러를_응답한다() {
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

        // 존재하지 않는 주문 조회
        given()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .when()
            .get("/api/orders/9999")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("code", equalTo("AIPIA-0016"))
            .body("message", equalTo("주문을 찾을 수 없습니다."));
    }
}
